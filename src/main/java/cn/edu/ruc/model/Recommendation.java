package cn.edu.ruc.model;

import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;

import java.util.List;

public class Recommendation {
    private List<Entity> entityList;
    private List<List<Feature>> featureListList;

    public Recommendation(List<Entity> entityList, List<List<Feature>> featureListList) {
        setEntityList(entityList);
        setFeatureListList(featureListList);
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Entity> entityList) {
        this.entityList = entityList;
    }

    public List<List<Feature>> getFeatureListList() {
        return featureListList;
    }

    public void setFeatureListList(List<List<Feature>> featureListList) {
        this.featureListList = featureListList;
    }

    @Override
    public String toString() {
        String s = "Recommendation{";
        s += "\n\tentityList=";
        for(Entity entity : getEntityList())
            s += "\n\t\t" + entity;
        s += "\n\tfeatureList=";
        for(List<Feature> featureList: getFeatureListList())
            for(Feature feature : featureList)
                s += "\n\t\t" + feature;
        s += "\n}";

        return s;
    }
}
