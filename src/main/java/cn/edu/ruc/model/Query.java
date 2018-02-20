package cn.edu.ruc.model;

import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;

import java.util.List;

public class Query {
    private List<Entity> entityList;
    private List<Feature> featureList;

    public Query(List<Entity> entityList, List<Feature> featureList) {
        setEntityList(entityList);
        setFeatureList(featureList);
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Entity> entityList) {
        this.entityList = entityList;
    }

    public List<Feature> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<Feature> featureList) {
        this.featureList = featureList;
    }

    @Override
    public String toString() {
        String s = "\n\tQuery{";
        s += "\n\t\tentityList=";
        for(Entity entity : getEntityList())
            s += "\n\t\t\t" + entity;
        s += "\n\t\tfeatureList=";
        for(Feature feature : getFeatureList())
            s += "\n\t\t\t" + feature;
        s += "\n\t}";

        return s;
    }
}
