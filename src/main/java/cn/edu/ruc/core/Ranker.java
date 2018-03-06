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
	public static List<Entity> getRelevantEntityList(List<Entity> queryEntityList, List<Feature> queryFeatureList) {
		//important: hash map is not thread-safe.
		Map<Integer, Entity> targetEntityMap = new ConcurrentHashMap<>();

		if(queryEntityList != null && queryEntityList.size() > 0) {
			getRelevantEntityListByEntity(queryEntityList, false).parallelStream() //can apply parallel steam here, since a query entity can not produce two same target entity
					.forEach(targetEntity -> {
						if (targetEntityMap.containsKey(targetEntity.getId())) {
							targetEntityMap.get(targetEntity.getId()).setScore(targetEntityMap.get(targetEntity.getId()).getScore() + targetEntity.getScore());
						} else {
							targetEntityMap.put(targetEntity.getId(), targetEntity);
						}
					});
		}

		if(queryFeatureList != null && queryFeatureList.size() > 0) {
			getRelevantEntityListByFeature(queryFeatureList, true).parallelStream() //can apply parallel steam here, since a query entity can not produce two same target entity
					.forEach(targetEntity -> {
						if (targetEntityMap.containsKey(targetEntity.getId())) {
							targetEntityMap.get(targetEntity.getId()).setScore(targetEntityMap.get(targetEntity.getId()).getScore() + targetEntity.getScore());
						} else {
							targetEntityMap.put(targetEntity.getId(), targetEntity);
						}
					});

			if(targetEntityMap.size() < DataUtil.Output_Entity_Size) {
				getRelevantEntityListByFeature(queryFeatureList, false).parallelStream() //can apply parallel steam here, since a query entity can not produce two same target entity
						.forEach(targetEntity -> {
							if (targetEntityMap.containsKey(targetEntity.getId())) {
								targetEntityMap.get(targetEntity.getId()).setScore(targetEntityMap.get(targetEntity.getId()).getScore() + targetEntity.getScore());
							} else {
								targetEntityMap.put(targetEntity.getId(), targetEntity);
							}
						});
			}
		}

		List<Entity> relevantEntityList = sortEntityList(new ArrayList<>(targetEntityMap.values()), DataUtil.Output_Entity_Size);

		for(int i = 0; i < relevantEntityList.size(); i ++) {
			relevantEntityList.get(i).setRank(i + 1);
			//relevantEntityList.get(i).setScore(relevantEntityList.get(i).getScore() / (queryEntityList.size() + queryFeatureList.size()));
			Parser.decodeEntity(relevantEntityList.get(i));
			Parser.decodeDescription(relevantEntityList.get(i));
		}

		return relevantEntityList;
	}

	//get relevant entity list of an entity list
	private static List<Entity> getRelevantEntityListByEntity(List<Entity> queryEntityList, boolean isConnected) {
		Set<Integer> sourceEntityIdSet = new HashSet<>();
		if(isConnected) {
			for(Entity queryEntity : queryEntityList)
				sourceEntityIdSet.addAll(DataUtil.getSourceEntityIdSet(queryEntity.getId()));
		}

		return DataUtil.getWholeSourceEntityIdList().parallelStream()
				.filter(targetEntityId -> isConnected ? sourceEntityIdSet.contains(targetEntityId) : true)
				.map(targetEntityId -> {
					//important: consider the weight of queryEntity and queryFeature
					double score = queryEntityList.parallelStream()
							.map(queryEntity -> Computer.getEmbeddingScore(targetEntityId, queryEntity.getId()) * queryEntity.getScore())
							.reduce(0.0, (s1, s2) -> (s1 + s2));

					return new Entity(targetEntityId, score);
				})
				.collect(Collectors.toList());
	}

	//get relevant entity list of a feature list
	private static List<Entity> getRelevantEntityListByFeature(List<Feature> queryFeatureList, boolean isConnected){
		Set<Integer> sourceEntityIdSet = new HashSet<>();
		if(isConnected) {
			for (Feature queryFeature : queryFeatureList)
				sourceEntityIdSet.addAll(DataUtil.getTargetEntityIdSet(queryFeature.getEntity().getId(), queryFeature.getRelation().getId(), queryFeature.getRelation().getDirection()));
		}

		return DataUtil.getWholeSourceEntityIdList().parallelStream()
				.filter(targetEntityId -> isConnected ? sourceEntityIdSet.contains(targetEntityId) : true)
				.map(targetEntityId -> {
					//important: consider the weight of queryEntity and queryFeature
					double score = queryFeatureList.parallelStream()
							.map(queryFeature -> isConnected ? ((DataUtil.getTargetEntityIdSet(queryFeature.getEntity().getId(), queryFeature.getRelation().getId(), queryFeature.getRelation().getDirection()).contains(targetEntityId)) ? Computer.getEmbeddingsScore(targetEntityId, queryFeature.getRelation().getId(), queryFeature.getRelation().getDirection(), queryFeature.getEntity().getId()) * queryFeature.getScore() : 0.0) : Computer.getEmbeddingsScore(targetEntityId, queryFeature.getRelation().getId(), queryFeature.getRelation().getDirection(), queryFeature.getEntity().getId()) * queryFeature.getScore())
							.reduce(0.0, (s1, s2) -> (s1 + s2));

					return new Entity(targetEntityId, score);
				})
				.collect(Collectors.toList());
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
				//relevantFeatureListList.get(i).get(j).setScore(relevantFeatureListList.get(i).get(j).getScore() / queryEntityList.size());
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
			for(Map.Entry<Integer, Set<Integer>> relation2entityEntry : DataUtil.getRelation2EntityMap(queryEntity.getId(), direction).entrySet()){
				int num = relation2entityEntry.getValue().size();
				min_size = min_size > num ? num : min_size;
				max_size = max_size < num ? num : max_size;
			}
		}

		List<Relation> relationList = new ArrayList<>();
		for(int direction : DataUtil.Directions){
			for(Map.Entry<Integer, Set<Integer>> relation2entityEntry : DataUtil.getRelation2EntityMap(queryEntity.getId(), direction).entrySet()){
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
			targetEntityIdSet.addAll(DataUtil.getTargetEntityIdSet(queryEntity.getId(), relation.getId(), relation.getDirection()));

		double[] queryEntityVector = Computer.getAverageEmbedding(queryEntityList);

		return DataUtil.getWholeTargetEntityIdList().parallelStream()
				.filter(targetEntityId -> isConnected ? targetEntityIdSet.contains(targetEntityId) : !targetEntityIdSet.contains(targetEntityId))
				.map(targetEntityId -> {
					//important: consider the weight of queryEntity and queryFeature
					double score = isConnected ?  queryEntityList.parallelStream()
							.map(queryEntity -> (DataUtil.getTargetEntityIdSet(queryEntity.getId(), relation.getId(), relation.getDirection()).contains(targetEntityId)) ? Computer.getEmbeddingsScore(targetEntityId, relation.getId(), relation.getDirection(), queryEntity.getId()) * queryEntity.getScore() : 0.0)
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

	//get relevant feature of an entity list by specifying a relation
	/*private static List<Feature> getRelevantFeatureList(List<Entity> queryEntityList, Relation queryRelation, boolean isConnected) {
		//important: hash map is not thread-safe.
		Map<Integer, Feature> featureMap = new ConcurrentHashMap<>();

		queryEntityList.stream() //can not apply parallel steam here, or may occur un-consistence of write and read, consider the lock in the future
					.forEach(queryEntity -> getRelevantFeatureList(queryEntity, queryRelation, isConnected).parallelStream() //can apply parallel steam here, since a query entity can not produce two same feature
							.forEach(feature -> {
								if(featureMap.containsKey(feature.getEntity().getId())) {
									featureMap.get(feature.getEntity().getId()).getEntity().setScore(featureMap.get(feature.getEntity().getId()).getEntity().getScore() + feature.getEntity().getScore());
								}
								else {
									featureMap.put(feature.getEntity().getId(), feature);
								}
							}));

		featureMap.values().parallelStream()
				.forEach(feature -> {
						feature.getEntity().setScore(feature.getEntity().getScore() / queryEntityList.size());
						feature.setScore(feature.getRelation().getScore() * feature.getEntity().getScore());
					});

		return sortFeatureList(new ArrayList<>(featureMap.values()));
	}

	//get relevant feature of an entity by specifying a relation
	private static List<Feature> getRelevantFeatureList(Entity queryEntity, Relation queryRelation, boolean isConnected) {
		//important: the direction of the features should be inverse
		Relation targetRelation = new Relation(queryRelation.getId(), - queryRelation.getDirection(), queryRelation.getScore());

		return sortFeatureList(getRelevantEntityList(new Feature(queryEntity, queryRelation, queryEntity.getScore()), isConnected).parallelStream()
				.map(targetEntity -> new Feature(targetEntity, targetRelation, targetEntity.getScore() * targetRelation.getScore()))
				.collect(Collectors.toList()));
	}
	*/
}	
