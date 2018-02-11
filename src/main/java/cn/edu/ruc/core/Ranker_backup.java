package cn.edu.ruc.core;

import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;
import cn.edu.ruc.domain.Relation;

import java.util.*;
import java.util.stream.Collectors;

public class Ranker_backup {
	//get relevant entity list using graph embedding model
	/*public static List<Entity> getRelevantEntityList(List<Entity> queryEntityList, List<Feature> queryFeatureList) {
		List<Entity> relevantEntityList = DataUtil.getEntitySet().parallelStream()
				.map(similarEntityId -> {
					double score = 0;
					//important: consider the weight of queryEntity and queryFeature
					score += queryEntityList.parallelStream()
							.map(queryEntity -> queryEntity.getScore() * computeScore(similarEntityId, queryEntity.getId()))
							.reduce(0.0, (s1, s2) -> (s1 + s2));
					score += queryFeatureList.parallelStream()
							.map(queryFeature -> queryFeature.getScore() * computeScore(similarEntityId, queryFeature.getRelation().getId(), queryFeature.getRelation().getDirection(), queryFeature.getEntity().getId()))
							.reduce(0.0, (s1, s2) -> (s1 + s2));

					return new Entity(similarEntityId, score);
				})
				.collect(Collectors.toList());

		relevantEntityList.parallelStream()
				.forEach(entity -> entity.setScore(entity.getScore() / (queryEntityList.size() + queryFeatureList.size())));

		relevantEntityList = sortEntityList(relevantEntityList);

		relevantEntityList.parallelStream()
				.forEach(entity -> Parser.decodeEntity(entity));

		return relevantEntityList;
	}

	//get relevant entity list using graph embedding model
	public static List<Entity> getRelevantEntityList(List<Entity> queryEntityList, List<Feature> queryFeatureList) {
		Map<Integer, Entity> entityMap = new HashMap<>();

		queryEntityList.parallelStream()
				.forEach(queryEntity -> getRelevantEntityList(queryEntity).parallelStream()
						.forEach(entity -> {
							if(entityMap.containsKey(entity.getId())) {
								entityMap.get(entity.getId()).setScore(entityMap.get(entity.getId()).getScore() + entity.getScore());
							}
							else {
								entityMap.put(entity.getId(), entity);
							}
						}));

		queryFeatureList.parallelStream()
				.forEach(queryFeature -> getRelevantEntityList(queryFeature).parallelStream()
						.forEach(entity -> {
							if(entityMap.containsKey(entity.getId())) {
								entityMap.get(entity.getId()).setScore(entityMap.get(entity.getId()).getScore() + entity.getScore());
							}
							else {
								entityMap.put(entity.getId(), entity);
							}
						}));

		List<Entity> relevantEntityList = sortEntityList(new ArrayList<>(entityMap.values()));

		relevantEntityList.parallelStream()
				.forEach(entity -> entity.setScore(entity.getScore() / (queryEntityList.size() + queryFeatureList.size())));

		relevantEntityList.parallelStream()
				.forEach(entity -> Parser.decodeEntity(entity));

		return relevantEntityList;
	}

	//get relevant entity list of an entity, where the entities should not necessarily connect with this feautre
	private static List<Entity> getRelevantEntityList(Entity queryEntity) {
		return DataUtil.getEntitySet().parallelStream()
				.map(similarEntityId -> {
					//important: consider the weight of queryEntity and queryFeature
					double score = computeScore(similarEntityId, queryEntity.getId()) * queryEntity.getScore();

					return new Entity(similarEntityId, score);
				})
				.collect(Collectors.toList());
	}


	//get relevant entity list of a feature, where the entities should connect with this feature
	private static List<Entity> getRelevantEntityList(Feature queryFeature){
		return DataUtil.getEntitySet(queryFeature.getEntity().getId(), queryFeature.getRelation().getId(), queryFeature.getRelation().getDirection()).parallelStream()
				.map(targetEntityId -> {
					//important: consider the weight of queryEntity and queryFeature
					double score = computeScore(targetEntityId, queryFeature.getRelation().getId(), queryFeature.getRelation().getDirection(), queryFeature.getEntity().getId()) * queryFeature.getScore();

					return new Entity(targetEntityId, score);
				}).collect(Collectors.toList());
	}


	////get relevant feature of an entity by its all relations, and classify them as relations by information gain
	public static List<List<Feature>> getRelevantFeatureList(List<Entity> queryEntityList) {
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
		Map<Integer, Feature> featureMap = new HashMap<>();
		queryEntityList.parallelStream()
					.forEach(queryEntity -> getRelevantFeatureList(queryEntity, queryRelation).parallelStream()
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

	//get relevant feature of an entity by its all relations, and classify them as relations by information gain
	private static List<List<Feature>> getRelevantFeatureList(Entity queryEntity) {
		List<List<Feature>> relevantFeatureList = getRelevantRelation(queryEntity).stream()
				.map(queryRelation -> getRelevantFeatureList(queryEntity, queryRelation))
				.collect(Collectors.toList());

		relevantFeatureList.parallelStream()
				.forEach(featureList -> featureList.parallelStream()
						.forEach(feature -> Parser.decodeFeature(feature)));

		return relevantFeatureList;
	}

	//get relevant feature of an entity by specifying a relation
	private static List<Feature> getRelevantFeatureList(Entity queryEntity, Relation queryRelation) {
		//important: the direction of the features should be inverse
		Relation targetRelation = new Relation(queryRelation.getId(), - queryRelation.getDirection(), queryRelation.getScore());
		List<Feature> relevantFeatureList = sortFeatureList(getRelevantEntityList(queryEntity, queryRelation, queryEntity.getScore()).parallelStream()
				.forEach(targetEntity -> new Feature(targetEntity, targetRelation, targetEntity.getScore() * targetRelation.getScore()));

		return relevantFeatureList;
	}

	//get relevant relation of an entity list by accumulating their information gain
	private static List<Relation> getRelevantRelation(List<Entity> queryEntityList){
		HashMap<Integer, Relation> relationMap = new HashMap<>();
		queryEntityList.parallelStream()
				.forEach(queryEntity ->
					getRelevantRelation(queryEntity).parallelStream()
							.forEach(relation -> {
								if(relationMap.containsKey(relation.getId())) {
									relationMap.get(relation.getId()).setScore(relationMap.get(relation.getId()).getScore() + relation.getScore());
								}
								else {
									relationMap.put(relation.getId(), relation);
								}
							})
				);

		relationMap.values().parallelStream()
				.forEach(relation -> relation.setScore(relation.getScore() / queryEntityList.size()));

		return sortRelationList(new ArrayList<>(relationMap.values()));
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

	//compute score in the embedding space
	private static double computeScore(int entityId1, int entityId2) {
		double[] entityVector1 = DataUtil.getEntity2Vector(entityId1);
		double[] entityVector2 = DataUtil.getEntity2Vector(entityId2);

		double sum=0;
		for(int i = 0 ; i < DataUtil.Dimension; i ++) {
			sum += Math.pow(entityVector1[i] - entityVector2[i], 2);
		}

		return (Math.sqrt(DataUtil.Dimension) - Math.sqrt(sum))/ Math.sqrt(DataUtil.Dimension);
	}

	private static double computeScore(int entityId1, int relationId, int direction, int entityId2){
		double[] entityVector1 = DataUtil.getEntity2Vector(entityId1);
		double[] relationVector = DataUtil.getRelation2Vector(relationId);
		double[] entityVector2 = DataUtil.getEntity2Vector(entityId2);

		double sum=0;
		for(int i = 0 ; i < DataUtil.Dimension; i ++) {
			sum += Math.pow(entityVector1[i] - (direction * relationVector[i] + entityVector2[i]), 2);
		}

		return (Math.sqrt(DataUtil.Dimension) - Math.sqrt(sum))/ Math.sqrt(DataUtil.Dimension);
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


	//get relevant feature list using graph embedding model and information gain
	/*public static List<List<Feature>> getRelevantFeatureList(List<Entity> queryEntityList) {
		List<List<Feature>> relevantFeatureList = getRelevantRelation(queryEntityList).stream()
				.map(queryRelation -> {
					Map<Integer, Entity> targetEntityMap = new HashMap<>();
					queryEntityList.parallelStream()
							.forEach(queryEntity -> DataUtil.getEntitySet(queryEntity.getId(), queryRelation.getId(), queryRelation.getDirection()).parallelStream()
									.forEach(targetEntityId -> {
										double score = computeScore(targetEntityId, queryRelation.getId(), queryRelation.getDirection(), queryEntity.getId()) * queryEntity.getScore();

										if(targetEntityMap.containsKey(targetEntityId)) {
											targetEntityMap.get(targetEntityId).setScore(targetEntityMap.get(targetEntityId).getScore() + score);
										}
										else {
											targetEntityMap.put(targetEntityId, new Entity(targetEntityId, score));
										}
									})
							);

					//important: the direction of the features should be inverse
					Relation targetRelation = new Relation(queryRelation.getId(), - queryRelation.getDirection(), queryRelation.getScore());
					List<Feature> relevantFeatureList_tmp = targetEntityMap.values().stream()
							.map(targetEntity -> new Feature(targetEntity, targetRelation, targetEntity.getScore() * targetRelation.getScore()))
							.collect(Collectors.toList());

					return sortFeatureList(relevantFeatureList_tmp);
				})
				.collect(Collectors.toList());

		relevantFeatureList.parallelStream()
				.forEach(featureList -> featureList.parallelStream()
						.forEach(feature -> Parser.decodeFeature(feature)));

		return relevantFeatureList;
	}

	private static List<Relation> getRelevantRelation(List<Entity> queryEntityList){
		int min_size = Integer.MAX_VALUE;
		int max_size = 0;
		for(Entity queryEntity : queryEntityList){
			for(int direction : DataUtil.Directions){
				for(Map.Entry<Integer, Set<Integer>> relation2entityEntry : DataUtil.getRelation2EntityMap(queryEntity.getId(), direction).entrySet()){
					int num = relation2entityEntry.getValue().size();
					min_size = min_size > num ? num : min_size;
					max_size = max_size < num ? num : max_size;
				}
			}
		}

		int size = min_size + max_size;
		HashMap<Integer, Relation> relationMap = new HashMap<>();
		for(Entity queryEntity : queryEntityList){
			for(int direction : DataUtil.Directions){
				for(Map.Entry<Integer, Set<Integer>> relation2entityEntry : DataUtil.getRelation2EntityMap(queryEntity.getId(), direction).entrySet()){
					double score = (double) relation2entityEntry.getValue().size() / size;
					score = (- score * Math.log(score)) * queryEntity.getScore();

					if(relationMap.containsKey(relation2entityEntry.getKey())) {
						relationMap.get(relation2entityEntry.getKey()).setScore(relationMap.get(relation2entityEntry.getKey()).getScore() + score);
					}
					else {
						relationMap.put(relation2entityEntry.getKey(), new Relation(relation2entityEntry.getKey(), direction, score));
					}
				}
			}
		}

		return sortRelationList(new ArrayList<>(relationMap.values()));
	}*/
}	
