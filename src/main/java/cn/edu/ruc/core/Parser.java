package cn.edu.ruc.core;

import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;
import cn.edu.ruc.domain.Relation;


public class Parser {
    public static Entity encodeEntity(String entityString){
        Entity entity = new Entity(DataUtil.getEntity2Id(entityString), entityString);

        return entity != null ? entity : null;
    }

    public static Entity encodeEntity(String entityString, double score){
        Entity entity = new Entity(DataUtil.getEntity2Id(entityString), entityString, score);

        return entity != null ? entity : null;
    }

    public static Relation encodeRelation(String relationString, int direction){
        Relation relation = new Relation(DataUtil.getRelation2Id(relationString), direction, relationString);

        return relation != null ? relation : null;
    }

    public static Relation encodeRelation(String relationString, int direction, double score){
        Relation relation = new Relation(DataUtil.getRelation2Id(relationString), direction, relationString, score);

        return relation != null ? relation : null;
    }

    public static Feature encodeFeature(String featureString, double score){
        String[] tokens = featureString.split("##");
        String entityString = tokens[0];
        String relationString = tokens[1];
        int relationDirection = (tokens.length == 3) ? Integer.parseInt(tokens[2]) : -1;

        Entity entity = encodeEntity(entityString, score);
        Relation relation = encodeRelation(relationString, relationDirection, score);

        return entity != null && relation != null ? new Feature(entity, relation, score) : null;
    }

    public static void decodeEntity(Entity entity){
        entity.setName(DataUtil.getId2Entity(entity.getId()));
        entity.setDescription(DataUtil.getDescription(entity.getId()));
        entity.setImage(DataUtil.getImage(entity.getId()));
    }


    public static void decodeRelation(Relation relation){
        relation.setName(DataUtil.getId2Relation(relation.getId()));
    }


    public static void decodeFeature(Feature feature){
        decodeEntity(feature.getEntity());
        decodeRelation(feature.getRelation());
    }
}
