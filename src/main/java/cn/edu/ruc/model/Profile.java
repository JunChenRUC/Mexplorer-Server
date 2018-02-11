package cn.edu.ruc.model;

import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;

import java.util.List;

public class Profile {
    private Entity entity;
    private List<List<Feature>> featureListList;

    public Profile(Entity entity, List<List<Feature>> featureListList) {
        setEntity(entity);
        setFeatureListList(featureListList);
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public List<List<Feature>> getFeatureListList() {
        return featureListList;
    }

    public void setFeatureListList(List<List<Feature>> featureListList) {
        this.featureListList = featureListList;
    }

    @Override
    public String toString() {
        String s = "Profile{";
        s += "\n\tentity=\n\t\t" + getEntity() + "\n\tfeatureList=";
        for(List<Feature> featureList: getFeatureListList())
            for(Feature feature : featureList)
                s += "\n\t\t" + feature;
        s += "\n}";

        return s;
    }
}
