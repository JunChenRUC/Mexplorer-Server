package cn.edu.ruc.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.*;


public class DictionaryManager {
	private Map<String, Integer> source2id = new HashMap<>();
	private Map<Integer, String> id2source = new HashMap<>();
	private Map<String, Integer> target2id = new HashMap<>();
	private Map<Integer, String> id2target = new HashMap<>();
	private Map<String, Integer> relation2id = new HashMap<>();
	private Map<Integer, String> id2relation = new HashMap<>();

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

				if(Integer.parseInt(tokens[2]) == 0) {
					source2id.put(name, Integer.parseInt(tokens[1]));
					id2source.put(Integer.parseInt(tokens[1]), name);
				}
				else if(Integer.parseInt(tokens[2]) == 1) {
					target2id.put(name, Integer.parseInt(tokens[1]));
					id2target.put(Integer.parseInt(tokens[1]), name);
				}
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
	
	public Map<String, Integer> getSource2Id(){
		return source2id;
	}

	public Map<String, Integer> getTarget2Id(){
		return target2id;
	}

	public Map<String, Integer> getRelation2Id(){
		return relation2id;
	}

	public Map<Integer, String> getId2Source(){
		return id2source;
	}

	public Map<Integer, String> getId2Target(){
		return id2target;
	}
	
	public Map<Integer, String> getId2Relation(){
		return id2relation;
	}
}
