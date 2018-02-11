package cn.edu.ruc.domain;

public class Entity {
	private int id;
	private String name;
	private String description;
	private String image;
	double score;

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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "Entity{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", image='" + image + '\'' +
				", score=" + score +
				'}';
	}
}
