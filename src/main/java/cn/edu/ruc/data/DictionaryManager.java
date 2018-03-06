package cn.edu.ruc.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.*;


public class DictionaryManager {
	private Map<String, Integer> entity2id = new HashMap<>();
	private Map<Integer, String> id2entity = new HashMap<>();
	private Map<String, Integer> relation2id = new HashMap<>();
	private Map<Integer, String> id2relation = new HashMap<>();

	private List<Integer> sourceEntityIdList = new ArrayList<>();
	private List<Integer> targetEntityIdList = new ArrayList<>();

	public DictionaryManager(String inputPath_entity, String inputPath_relation){
		loadEntity(inputPath_entity);
		loadRelation(inputPath_relation);
	}

	public void loadEntity(String inputPath){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
			String tmpString;
			while((tmpString = reader.readLine()) != null) {
				String[] tokens = tmpString.split("\t");

				String name = URLDecoder.decode(tokens[0].replaceAll("_", " "), "UTF-8");

				entity2id.put(name, Integer.parseInt(tokens[1]));
				id2entity.put(Integer.parseInt(tokens[1]), name);

				if(Integer.parseInt(tokens[2]) == 0)
					sourceEntityIdList.add(Integer.parseInt(tokens[1]));
				else if(Integer.parseInt(tokens[2]) == 1)
					targetEntityIdList.add(Integer.parseInt(tokens[1]));
			}
			reader.close();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadRelation(String inputPath){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
        	String tmpString;
            while((tmpString = reader.readLine()) != null) {
            	String[] tokens = tmpString.split("\t");
            	
            	relation2id.put(tokens[0], Integer.parseInt(tokens[1]));
            	id2relation.put(Integer.parseInt(tokens[1]), tokens[0]);
            }
            reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String, Integer> getEntity2Id(){
		return entity2id;
	}
	
	public Map<String, Integer> getRelation2Id(){
		return relation2id;
	}

	public Map<Integer, String> getId2Entity(){
		return id2entity;
	}
	
	public Map<Integer, String> getId2Relation(){
		return id2relation;
	}

	public List<Integer> getSourceEntityIdList() {
		return sourceEntityIdList;
	}

	public List<Integer> getTargetEntityIdList() {
		return targetEntityIdList;
	}
}
