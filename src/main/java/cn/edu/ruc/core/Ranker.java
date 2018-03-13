/*
 * Copyright (c) 2018. by Chen Jun
 */

package cn.edu.ruc.core;

import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Relation;
import cn.edu.ruc.domain.Feature;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//important: pay attention to the thread-safe situation!
public class Ranker {
	/*---------------Relevant entity---------------*/
	//get relevant entity list using graph embedding model
	public static List<Entity> getRelevantEntityList(List<Entity> queryEntityList, List<Feature> queryFeatureList, boolean isFilter) {
		List<Entity> relevantEntityList = sortEntityList(getSourceIdSet(queryFeatureList, isFilter).parallelStream()
				.filter(sourceId -> DataUtil.hasDescription(sourceId))
				.map(sourceId -> {
					//important: consider the weight of queryEntity and queryFeature
					double score = 0;

					score += queryEntityList.parallelStream()
							.map(queryEntity -> Computer.getEmbeddingScore(sourceId, queryEntity.getId()) * queryEntity.getScore())
							.reduce(0.0, (s1, s2) -> (s1 + s2));

					score += queryFeatureList.parallelStream()
							.map(queryFeature -> Computer.getEmbeddingsScore(sourceId, queryFeature.getRelation().getId(), queryFeature.getRelation().getDirection(), queryFeature.getEntity().getId()) * queryFeature.getScore())
							.reduce(0.0, (s1, s2) -> (s1 + s2));

					return new Entity(sourceId, score);
				})
				.collect(Collectors.toList()), DataUtil.Output_Entity_Size);

		for(int i = 0; i < relevantEntityList.size(); i ++) {
			relevantEntityList.get(i).setRank(i + 1);
			Parser.decodeSource(relevantEntityList.get(i));
			Parser.decodeDescription(relevantEntityList.get(i));
		}

		return relevantEntityList;
	}

	public static Set<Integer> getSourceIdSet(List<Feature> queryFeatureList, boolean isFilter) {
		if(isFilter) {
			if(queryFeatureList.size() == 0) {
				return DataUtil.getWholeSourceIdSet();
			}
			else if(queryFeatureList.size() == 1) {
				return DataUtil.getSourceIdSet(queryFeatureList.get(0).getEntity().getId(), queryFeatureList.get(0).getRelation().getId(), queryFeatureList.get(0).getRelation().getDirection());
			}
			else {
				Set<Integer> sourceEntityIdSet = new HashSet<>();
				sourceEntityIdSet.addAll(DataUtil.getSourceIdSet(queryFeatureList.get(0).getEntity().getId(), queryFeatureList.get(0).getRelation().getId(), queryFeatureList.get(0).getRelation().getDirection()));
				for (int i = 1; i < queryFeatureList.size(); i ++)
					sourceEntityIdSet.retainAll(DataUtil.getTargetIdSet(queryFeatureList.get(i).getEntity().getId(), queryFeatureList.get(i).getRelation().getId(), queryFeatureList.get(i).getRelation().getDirection()));

				return sourceEntityIdSet;
			}
		}
		else {
			return DataUtil.getWholeSourceIdSet();
		}
	}

	/*---------------Relevant feature---------------*/
	//get relevant feature of an entity by its all relations, and classify them as relations by information gain
	public static List<List<Feature>> getRelevantFeatureListList(List<Entity> queryEntityList, boolean isConnected) {
		List<List<Feature>> relevantFeatureListList = getRelevantRelation(queryEntityList).stream()
				.map(queryRelation -> getRelevantFeatureList(queryEntityList, queryRelation, isConnected))
				.collect(Collectors.toList());

		for(int i = 0; i < relevantFeatureListList.size(); i ++) {
			for(int j = 0; j < relevantFeatureListList.get(i).size(); j ++) {
				relevantFeatureListList.get(i).get(j).setRank(j + 1);
				Parser.decodeFeature(relevantFeatureListList.get(i).get(j));
			}
		}

		return relevantFeatureListList;
	}

	private static List<Feature> getRelevantFeatureList(List<Entity> queryEntityList, Relation queryRelation, boolean isConnected) {
		//important: the direction of the features should be inverse
		Relation targetRelation = new Relation(queryRelation.getId(), - queryRelation.getDirection(), queryRelation.getScore());

		List<Feature> relevantFeatureList = getRelevantEntityListByFeature(queryEntityList, queryRelation, isConnected).parallelStream() //can apply parallel steam here, since a query entity can not produce two same feature
				.map(targetEntity -> new Feature(targetEntity, targetRelation, targetEntity.getScore() * targetRelation.getScore()))
				.collect(Collectors.toList());

		return sortFeatureList(relevantFeatureList, DataUtil.Output_Feature_Size);
	}

	//get relevant relation of an entity list by accumulating their information gain
	private static List<Relation> getRelevantRelation(List<Entity> queryEntityList){
		//important: hash map is not thread-safe.
		Map<Integer, Relation> relationMap = new ConcurrentHashMap<>();

		queryEntityList.stream() //can not apply parallel steam here, or may occur un-consistence of write and read, consider the lock in the future
				.forEach(queryEntity -> getRelevantRelation(queryEntity).parallelStream() //can apply parallel steam here, since a query entity can not produce two same relation via relation2entityMap
							.forEach(relation -> {
								if(relationMap.containsKey(relation.getId())) {
									relationMap.get(relation.getId()).setScore(relationMap.get(relation.getId()).getScore() + relation.getScore());
								}
								else {
									relationMap.put(relation.getId(), relation);
								}
							})
				);

		List<Relation> relevantRelationList = sortRelationList(new ArrayList<>(relationMap.values()), DataUtil.Output_Relation_Size);

		relevantRelationList.parallelStream()
				.forEach(relation -> relation.setScore(relation.getScore() / queryEntityList.size()));

		return relevantRelationList;
	}

	//get relevant relation of an entity by information gain
	private static List<Relation> getRelevantRelation(Entity queryEntity){
		int min_size = Integer.MAX_VALUE, max_size = 0;
		for(int direction : DataUtil.Directions){
			for(Map.Entry<Integer, Set<Integer>> relation2entityEntry : DataUtil.getRelation2TargetMap(queryEntity.getId(), direction).entrySet()){
				int num = relation2entityEntry.getValue().size();
				min_size = min_size > num ? num : min_size;
				max_size = max_size < num ? num : max_size;
			}
		}

		List<Relation> relationList = new ArrayList<>();
		for(int direction : DataUtil.Directions){
			for(Map.Entry<Integer, Set<Integer>> relation2entityEntry : DataUtil.getRelation2TargetMap(queryEntity.getId(), direction).entrySet()){
				double pro = (double) relation2entityEntry.getValue().size() / (min_size + max_size);
				double score = (- pro * Math.log(pro)) * queryEntity.getScore();

				relationList.add(new Relation(relation2entityEntry.getKey(), direction, score));
			}
		}

		return sortRelationList(relationList, DataUtil.Output_Relation_Size);
	}

	//get relevant entity list of a feature list, where the feature has a specific relation
	private static List<Entity> getRelevantEntityListByFeature(List<Entity> queryEntityList, Relation relation, boolean isConnected){
		Set<Integer> targetEntityIdSet = new HashSet<>();
		for(Entity queryEntity : queryEntityList)
			targetEntityIdSet.addAll(DataUtil.getTargetIdSet(queryEntity.getId(), relation.getId(), relation.getDirection()));

		double[] queryEntityVector = Computer.getAverageEmbedding(queryEntityList);

		return DataUtil.getWholeTargetIdSet().parallelStream()
				.filter(targetEntityId -> isConnected ? targetEntityIdSet.contains(targetEntityId) : !targetEntityIdSet.contains(targetEntityId))
				.map(targetEntityId -> {
					//important: consider the weight of queryEntity and queryFeature
					double score = isConnected ?  queryEntityList.parallelStream()
							.map(queryEntity -> (DataUtil.getTargetIdSet(queryEntity.getId(), relation.getId(), relation.getDirection()).contains(targetEntityId)) ? Computer.getEmbeddingsScore(targetEntityId, relation.getId(), relation.getDirection(), queryEntity.getId()) * queryEntity.getScore() : 0.0)
							.reduce(0.0, (s1, s2) -> (s1 + s2)) : Computer.getEmbeddingsScore(targetEntityId, relation.getId(), relation.getDirection(), queryEntityVector);

					return new Entity(targetEntityId, score);
				})
				.collect(Collectors.toList());
	}

	//return top-k
	private static List<Entity> sortEntityList(List<Entity> entityList, int k) {
		return entityList.parallelStream()
				.sorted((a, b) -> a.getScore() < b.getScore() ? 1 : -1)
				.limit(entityList.size() < k ? entityList.size() : k)
				.collect(Collectors.toList());
	}

	public static List<Relation> sortRelationList(List<Relation> relationList, int k){
		return relationList.parallelStream()
				.sorted((a, b) -> a.getScore() < b.getScore() ? 1 : -1)
				.limit(relationList.size() < k ? relationList.size() : k)
				.collect(Collectors.toList());
	}

	private static List<Feature> sortFeatureList(List<Feature> featureList, int k){
		return featureList.parallelStream()
				.sorted((a, b) -> a.getScore() < b.getScore() ? 1 : -1)
				.limit(featureList.size() < k ? featureList.size() : k)
				.collect(Collectors.toList());
	}
}	
