package cn.edu.ruc.model;

import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;

import java.util.List;

public class Result {
    private Query query;
    private List<Entity> entityList;
    private Profile profile;
    private List<List<Feature>> leftFeatureListList;
    private List<List<Feature>> rightFeatureListList;

    public Result(Query query, List<Entity> entityList, Profile profile, List<List<Feature>> leftFeatureListList, List<List<Feature>> rightFeatureListList) {
        setQuery(query);
        setEntityList(entityList);
        setProfile(profile);
        setLeftFeatureListList(leftFeatureListList);
        setRightFeatureListList(rightFeatureListList);
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

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<List<Feature>> getLeftFeatureListList() {
        return leftFeatureListList;
    }

    public void setLeftFeatureListList(List<List<Feature>> leftFeatureListList) {
        this.leftFeatureListList = leftFeatureListList;
    }

    public List<List<Feature>> getRightFeatureListList() {
        return rightFeatureListList;
    }

    public void setRightFeatureListList(List<List<Feature>> rightFeatureListList) {
        this.rightFeatureListList = rightFeatureListList;
    }


    @Override
    public String toString() {
        String s = "\nResult{";
        s += "\n\tquery=" + query;
        s += "\n\tentityList=";
        for(Entity entity : getEntityList())
            s += "\n\t\t" + entity;
        s += "\n\tprofile=" + profile;
        s += "\n\tleftFeatureList=";
        for(List<Feature> featureList: getLeftFeatureListList())
            for(Feature feature : featureList)
                s += "\n\t\t" + feature;
        s += "\n\trightFeatureList=";
        for(List<Feature> featureList: getRightFeatureListList())
            for(Feature feature : featureList)
                s += "\n\t\t" + feature;
        s += "\n}";

        return s;
    }
}
