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

public class Ranker {
	//important: pay attention to the thread-safe situation!

	/*---------------Relevant entity---------------*/
	//get relevant entity list using graph embedding model
	public static List<Entity> getRelevantEntityList(List<Entity> queryEntityList, List<Feature> queryFeatureList) {
		//important: hash map is not thread-safe.
		Map<Integer, Entity> targetEntityMap = new ConcurrentHashMap<>();

		queryEntityList.stream() //can not apply parallel steam here, or may occur un-consistence of write and read, consider the lock in the future
				.forEach(queryEntity -> getRelevantEntityList(queryEntity, false).parallelStream() //can apply parallel steam here, since a query entity can not produce two same target entity
						.forEach(targetEntity -> {
							if(targetEntityMap.containsKey(targetEntity.getId())) {
								targetEntityMap.get(targetEntity.getId()).setScore(targetEntityMap.get(targetEntity.getId()).getScore() + targetEntity.getScore());
							}
							else {
								targetEntityMap.put(targetEntity.getId(), targetEntity);
							}
						}));


		queryFeatureList.stream() //can not apply parallel steam here, or may occur un-consistence of write and read, consider the lock in the future
				.forEach(queryFeature -> getRelevantEntityList(queryFeature, false).parallelStream() //can apply parallel steam here, since a query entity can not produce two same target entity
						.forEach(targetEntity -> {
							if(targetEntityMap.containsKey(targetEntity.getId())) {
								targetEntityMap.get(targetEntity.getId()).setScore(targetEntityMap.get(targetEntity.getId()).getScore() + targetEntity.getScore());
							}
							else {
								targetEntityMap.put(targetEntity.getId(), targetEntity);
							}
						}));

		List<Entity> relevantEntityList = sortEntityList(new ArrayList<>(targetEntityMap.values()));

		//normalize the score of each entity, and parse their names
		relevantEntityList.parallelStream()
				.forEach(entity -> {
					entity.setScore(entity.getScore() / (queryEntityList.size() + queryFeatureList.size()));
					Parser.decodeEntity(entity);
					Parser.decodeDescription(entity);
				});

		return relevantEntityList;
	}

	//get relevant entity list of an entity, where the entities should not necessarily connect with this feature
	private static List<Entity> getRelevantEntityList(Entity queryEntity, boolean isConnected) {
		Set<Integer> targetEntitySet = isConnected ? DataUtil.getEntitySet(queryEntity.getId()) : DataUtil.getEntitySet();

		return targetEntitySet.parallelStream()
					.map(targetEntityId -> {
						//important: consider the weight of queryEntity and queryFeature
						double score = Computer.getEmbeddingScore(targetEntityId, queryEntity.getId()) * queryEntity.getScore();

						return new Entity(targetEntityId, score);
					})
					.collect(Collectors.toList());
	}

	//get relevant entity list of a feature, where the entities should connect with this feature
	private static List<Entity> getRelevantEntityList(Feature queryFeature, boolean isConnected){
		Set<Integer> targetEntitySet = isConnected ? DataUtil.getEntitySet(queryFeature.getEntity().getId(), queryFeature.getRelation().getId(), queryFeature.getRelation().getDirection()) : DataUtil.getEntitySet();

		return targetEntitySet.parallelStream()
				.map(targetEntityId -> {
					//important: consider the weight of queryEntity and queryFeature
					double score = Computer.getEmbeddingsScore(targetEntityId, queryFeature.getRelation().getId(), queryFeature.getRelation().getDirection(), queryFeature.getEntity().getId()) * queryFeature.getScore();

					return new Entity(targetEntityId, score);
				})
				.collect(Collectors.toList());
	}

	/*---------------Relevant feature---------------*/
	//get relevant feature of an entity by its all relations, and classify them as relations by information gain
	public static List<List<Feature>> getRelevantFeatureListList(List<Entity> queryEntityList) {
		List<List<Feature>> relevantFeatureList = getRelevantRelation(queryEntityList).stream()
				.map(queryRelation -> getRelevantFeatureList(queryEntityList, queryRelation))
				.collect(Collectors.toList());

		relevantFeatureList.parallelStream()
				.forEach(featureList -> featureList.parallelStream()
						.forEach(feature -> Parser.decodeFeature(feature)));

		return relevantFeatureList;
	}

	//get relevant feature of an entity list by specifying a relation
	private static List<Feature> getRelevantFeatureList(List<Entity> queryEntityList, Relation queryRelation) {
		//important: hash map is not thread-safe.
		Map<Integer, Feature> featureMap = new ConcurrentHashMap<>();

		queryEntityList.stream() //can not apply parallel steam here, or may occur un-consistence of write and read, consider the lock in the future
					.forEach(queryEntity -> getRelevantFeatureList(queryEntity, queryRelation).parallelStream() //can apply parallel steam here, since a query entity can not produce two same feature
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
	private static List<Feature> getRelevantFeatureList(Entity queryEntity, Relation queryRelation) {
		//important: the direction of the features should be inverse
		Relation targetRelation = new Relation(queryRelation.getId(), - queryRelation.getDirection(), queryRelation.getScore());

		return sortFeatureList(getRelevantEntityList(new Feature(queryEntity, queryRelation, queryEntity.getScore()), true).parallelStream()
				.map(targetEntity -> new Feature(targetEntity, targetRelation, targetEntity.getScore() * targetRelation.getScore()))
				.collect(Collectors.toList()));
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

		List<Relation> relevantRelationList = sortRelationList(new ArrayList<>(relationMap.values()));

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

		return sortRelationList(relationList);
	}

	//return top-k
	private static List<Entity> sortEntityList(List<Entity> entityList) {
		return entityList.parallelStream()
				.sorted((a, b) -> a.getScore() < b.getScore() ? 1 : -1)
				.limit(entityList.size() < DataUtil.Output_Entity_Size ? entityList.size() : DataUtil.Output_Entity_Size)
				.collect(Collectors.toList());
	}

	public static List<Relation> sortRelationList(List<Relation> relationList){
		return relationList.parallelStream()
				.sorted((a, b) -> a.getScore() < b.getScore() ? 1 : -1)
				.limit(relationList.size() < DataUtil.Output_Relation_Size ? relationList.size() : DataUtil.Output_Relation_Size)
				.collect(Collectors.toList());
	}

	private static List<Feature> sortFeatureList(List<Feature> featureList){
		return featureList.parallelStream()
				.sorted((a, b) -> a.getScore() < b.getScore() ? 1 : -1)
				.limit(featureList.size() < DataUtil.Output_Feature_Size ? featureList.size() : DataUtil.Output_Feature_Size)
				.collect(Collectors.toList());
	}
}	
