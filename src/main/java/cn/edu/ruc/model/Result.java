package cn.edu.ruc.model;

import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;

import java.util.List;

public class Result {
    private Query query;
    private List<Entity> entityList;
    private List<List<Feature>> featureListList;
    private Profile profile;

    public Result(Query query, List<Entity> entityList, List<List<Feature>> featureListList, Profile profile) {
        setQuery(query);
        setEntityList(entityList);
        setFeatureListList(featureListList);
        setProfile(profile);
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
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

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        String s = "\nResult{";
        s += "\n\tquery=" + query;
        s += "\n\tentityList=";
        for(Entity entity : getEntityList())
            s += "\n\t\t" + entity;
        s += "\n\tfeatureList=";
        for(List<Feature> featureList: getFeatureListList())
            for(Feature feature : featureList)
                s += "\n\t\t" + feature;
        s += "\n\tprofile=" + profile;
        s += "\n}";

        return s;
    }
}
