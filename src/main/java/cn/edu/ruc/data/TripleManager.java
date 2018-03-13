package cn.edu.ruc.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TripleManager {
	private Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>> direction2tripleMap = new HashMap<>();

	public TripleManager(String inputPath_triple, DictionaryManager dictionary){
		loadTriple(inputPath_triple, dictionary);
	}
	
	public void loadTriple(String inputPath, DictionaryManager dictionary){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
        	String tmpString;
            while((tmpString = reader.readLine()) != null) {
            	String[] tokens = tmpString.split("\t");
            	String subject = URLDecoder.decode(tokens[0], "UTF-8").replaceAll("_", " ");
            	String predicate = URLDecoder.decode(tokens[1], "UTF-8").replaceAll("_", " ");
            	String object = URLDecoder.decode(tokens[2], "UTF-8").replaceAll("_", " ");

            	int subjectId = dictionary.getSource2Id().get(subject);
            	int predicateId = dictionary.getRelation2Id().get(predicate);
            	int objectId = dictionary.getTarget2Id().get(object);

            	for (int direction : new int[]{1, -1}){
            		if(!direction2tripleMap.containsKey(direction))
            			direction2tripleMap.put(direction, new HashMap<>());

					Map<Integer, Map<Integer, Set<Integer>>> tripleMap = direction2tripleMap.get(direction);
					if(direction == -1){
						int tmp = subjectId;
						subjectId = objectId;
						objectId = tmp;
					}

					if(!tripleMap.containsKey(subjectId)){
						tripleMap.put(subjectId , new HashMap<>());
						tripleMap.get(subjectId).put(predicateId, new HashSet<>());
					}
					else if(!tripleMap.get(subjectId).containsKey(predicateId))
						tripleMap.get(subjectId).put(predicateId, new HashSet<>());

					tripleMap.get(subjectId).get(predicateId).add(objectId);
				}
            }
            reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>> getDirection2TripleMap(){
		return direction2tripleMap;
	}
}
