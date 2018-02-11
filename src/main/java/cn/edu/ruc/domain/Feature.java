package cn.edu.ruc.domain;

public class Feature {
    private Entity entity;
    private Relation relation;
    private double score;

    public Feature(Entity entity, Relation relation){
        setEntity(entity);
        setRelation(relation);
        setScore(1);
    }

    public Feature(Entity entity, Relation relation, double score){
        setEntity(entity);
        setRelation(relation);
        setScore(score);
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof Feature))
            return false;
        if(object == this)
            return true;

        if(entity.getId() != ((Feature) object).getEntity().getId())
            return false;
        if (relation.getDirection() != ((Feature) object).getRelation().getDirection() || relation.getId() != ((Feature) object).getRelation().getId())
            return false;

        return true;
    }

    public int hashCode(){
        int value = entity.getId() + relation.getId() * relation.getDirection();

        return value;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "relation=" + relation +
                ", entity=" + entity +
                ", score=" + score +
                '}';
    }
}
