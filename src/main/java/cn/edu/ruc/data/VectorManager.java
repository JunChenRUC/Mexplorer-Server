package cn.edu.ruc.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class VectorManager {
	public Map<Integer, double[]> entity2vector = new HashMap<>();
	public Map<Integer, double[]> relation2vector = new HashMap<>();

	public VectorManager(String inputPath_entity, String inputPath_relation, String dimension, DictionaryManager dictionaryManager){
		loadEntity2Vector(inputPath_entity, dimension, dictionaryManager);
		loadERelation2Vector(inputPath_relation, dimension, dictionaryManager);
	}

	public void loadEntity2Vector(String inputPath, String dimension, DictionaryManager dictionaryManager){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
			String tmpString;
			int count = 0;
			while((tmpString = reader.readLine()) != null) {
				if(dictionaryManager.getId2Entity().containsKey(count)){
					entity2vector.put(count, new double[Integer.parseInt(dimension)]);
					String[] tokens = tmpString.split("\t");
					for(int i = 0; i < tokens.length; i++)
						entity2vector.get(count)[i] = Double.parseDouble(tokens[i]);
				}else{
					System.out.println("Error: load entity vector!");
				}
				count++;
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Error: load entity vector!");
			e.printStackTrace();
		}
	}

	public void loadERelation2Vector(String inputPath, String dimension, DictionaryManager dictionaryManager){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
			String tmpString;
			int count = 0;
			while((tmpString = reader.readLine()) != null) {
				if(dictionaryManager.getId2Relation().containsKey(count)){
					relation2vector.put(count, new double[Integer.parseInt(dimension)]);
					String[] tokens = tmpString.split("\t");
					for(int i = 0; i < tokens.length; i++)
						relation2vector.get(count)[i] = Double.parseDouble(tokens[i]);
				}else{
					System.out.println("Error: load relation vector!");
				}
				count++;
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Error: load relation vector!");
			e.printStackTrace();
		}
	}
	
	public Map<Integer, double[]> getEntity2Vector(){
		return entity2vector;
	}
	
	public Map<Integer, double[]> getRelation2Vector(){
		return relation2vector;
	}
}
