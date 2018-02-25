package cn.edu.ruc.core;

import cn.edu.ruc.domain.Entity;

import java.util.List;

public class Computer {
    //compute score in the embedding space
    public static double getEmbeddingScore(int entityId1, int entityId2) {
        double[] entityVector1 = DataUtil.getEntity2Vector(entityId1);
        double[] entityVector2 = DataUtil.getEntity2Vector(entityId2);

        double sum=0;
        for(int i = 0 ; i < DataUtil.Dimension; i ++) {
            sum += Math.pow(entityVector1[i] - entityVector2[i], 2);
        }

        return (3 - Math.sqrt(sum))/ 3;
    }

    public static double getEmbeddingsScore(int entityId1, int relationId, int direction, int entityId2){
        double[] entityVector1 = DataUtil.getEntity2Vector(entityId1);
        double[] relationVector = DataUtil.getRelation2Vector(relationId);
        double[] entityVector2 = DataUtil.getEntity2Vector(entityId2);

        double sum=0;
        for(int i = 0 ; i < DataUtil.Dimension; i ++) {
            sum += Math.pow(entityVector1[i] - (direction * relationVector[i] + entityVector2[i]), 2);
        }

        return (3 - Math.sqrt(sum))/ 3;
    }

    public static double getEmbeddingsScore(int entityId1, int relationId, int direction, double[] entityVector2){
        double[] entityVector1 = DataUtil.getEntity2Vector(entityId1);
        double[] relationVector = DataUtil.getRelation2Vector(relationId);

        double sum = 0;
        for(int i = 0 ; i < DataUtil.Dimension; i ++) {
            sum += Math.pow(entityVector1[i] - (direction * relationVector[i] + entityVector2[i]), 2);
        }

        return (3 - Math.sqrt(sum))/ 3;
    }

    public static double[] getAverageEmbedding(List<Entity> entityList) {
        double[] entityVector = new double[DataUtil.Dimension];
        double bottom = 0;

        for(Entity entity : entityList) {
            bottom += entity.getScore();
            for(int i = 0; i < DataUtil.getEntity2Vector(entity.getId()).length; i ++)
                entityVector[i] += DataUtil.getEntity2Vector(entity.getId())[i] * entity.getScore();
        }

        for(int i = 0 ; i < DataUtil.Dimension; i ++) {
            entityVector[i] = entityVector[i] / bottom;
        }

        return entityVector;
    }
}
