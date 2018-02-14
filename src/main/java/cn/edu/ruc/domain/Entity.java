package cn.edu.ruc.domain;

import java.text.DecimalFormat;

public class Entity {
	private int id;
	private String name;
	private double score;
	private Description description;

	public Entity(int id){
		setId(id);
		setScore(1);
	}

	public Entity(int id, double score){
		setId(id);
		setScore(score);
	}

	public Entity(int id, String name){
		setId(id);
		setName(name);
		setScore(1);
	}

	public Entity(int id, String name, double score){
		setId(id);
		setName(name);
		setScore(score);
	}

	@Override
	public boolean equals(Object object){
		if(!(object instanceof Entity))
			return false;
		if(object == this)
			return true;

		if(getId() != ((Entity) object).getId())
			return false;

		return true;
	}

	public int hashCode(){
		int value = getId();

		return value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	@Override
	public String toString() {

		return "Entity{" +
				/*"id=" + id +*/
				"name='" + name + '\'' +
				", score=" + new DecimalFormat("0.000").format(score) +
				(description == null ? "" : ", description=" + description) +
				'}';
	}
}
