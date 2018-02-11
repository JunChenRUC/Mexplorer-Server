package cn.edu.ruc.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


public class ImageManager {
	public Map<Integer, String> entity2image = new HashMap<>();

	public ImageManager(String inputPath, DictionaryManager dictionaryManager){
		loadEntity2Image(inputPath, dictionaryManager);
	}

	public void loadEntity2Image(String inputPath,DictionaryManager dictionaryManager){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
			String tmpString;
			while((tmpString = reader.readLine()) != null) {
				String[] tokens = tmpString.split("\t");

				String entity = URLDecoder.decode(tokens[0], "UTF-8").replaceAll("_", " ");

				if(dictionaryManager.getEntity2Id().containsKey(entity)){
					entity2image.put(dictionaryManager.getEntity2Id().get(entity), tokens[1]);
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Error: load entity image!");
			e.printStackTrace();
		}
	}
	
	public Map<Integer, String> getEntity2Image(){
		return entity2image;
	}
}
