package cn.edu.ruc.core;

import cn.edu.ruc.domain.Description;
import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;
import cn.edu.ruc.domain.Relation;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    //important: pay attention to the strings such ""
    public static Entity encodeSource(String entityString) {
        if(entityString.contains("_"))
            entityString = entityString.replaceAll("_", ",");
        return DataUtil.getSource2Id(entityString) == -1 ? null : new Entity(DataUtil.getSource2Id(entityString), entityString);
    }

    public static List<Entity> encodeSourceList(List<String> entityStringList) {
        List<Entity> entityList = new ArrayList<>();

        for(String entityString : entityStringList) {
            String[] tokens = entityString.split("_");
            Entity entity = Parser.encodeSource(tokens[0]);

            if(entity != null && tokens.length == 2) {
                entity.setScore(Double.parseDouble(tokens[1]));
            }

            if(entity != null) {
                entityList.add(entity);
            }
        }

        return entityList;
    }

    public static Entity encodeTarget(String entityString) {
        if(entityString.contains("_"))
            entityString = entityString.replaceAll("_", ",");
        return DataUtil.getTarget2Id(entityString) == -1 ? null : new Entity(DataUtil.getTarget2Id(entityString), entityString);
    }

    public static Relation encodeRelation(String relationString, int direction) {
        return DataUtil.getRelation2Id(relationString) == -1 ? null : new Relation(DataUtil.getRelation2Id(relationString), direction, relationString);
    }

    public static Feature encodeFeature(String featureString) {
        String[] tokens = featureString.split("##");
        String entityString = tokens[0];
        String relationString = tokens[1];
        int relationDirection = (tokens.length == 3) ? Integer.parseInt(tokens[2]) : -1;

        Entity entity = encodeTarget(entityString);
        Relation relation = encodeRelation(relationString, relationDirection);

        return entity != null && relation != null ? new Feature(entity, relation) : null;
    }

    public static List<Feature> encodeFeatureList(List<String> featureStringList) {
        List<Feature> featureList = new ArrayList<>();

        for(String featureString : featureStringList) {
            String[] tokens = featureString.split("_");
            Feature feature = Parser.encodeFeature(tokens[0]);

            if(feature != null && tokens.length == 2) {
                feature.setScore(Double.parseDouble(tokens[1]));
            }

            if(feature != null) {
                featureList.add(feature);
            }
        }

        return featureList;
    }

    //decode
    public static void decodeSource(Entity entity){
        if(entity != null) {
            entity.setName(DataUtil.getId2Source(entity.getId()));
        }
    }

    public static void decodeTarget(Entity entity){
        if(entity != null) {
            entity.setName(DataUtil.getId2Target(entity.getId()));
        }
    }

    public static void decodeRelation(Relation relation){
        if(relation != null) {
            relation.setName(DataUtil.getId2Relation(relation.getId()));
        }
    }

    public static void decodeFeature(Feature feature){
        if(feature != null) {
            decodeTarget(feature.getEntity());
            decodeRelation(feature.getRelation());
        }
    }

    public static void decodeDescription(Entity entity){
        if(entity != null) {
            entity.setDescription(new Description(DataUtil.getPlot(entity.getId()), DataUtil.getImage(entity.getId()), DataUtil.getRating(entity.getId())));
        }
    }
}
