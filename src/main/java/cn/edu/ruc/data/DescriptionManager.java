package cn.edu.ruc.data;

import cn.edu.ruc.domain.Film;
import com.google.gson.Gson;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class DescriptionManager {
	public Map<Integer, String> entity2plot = new HashMap<>();
	public Map<Integer, String> entity2image = new HashMap<>();
	public Map<Integer, String> entity2rating = new HashMap<>();

	public DescriptionManager(String inputPath, DictionaryManager dictionaryManager){
		loadEntity2Description(inputPath, dictionaryManager);
	}

	public void loadEntity2Description(String inputPath,DictionaryManager dictionaryManager){
		try {
			Gson gson = new Gson();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
			String tmpString;
			while((tmpString = reader.readLine()) != null) {
				String[] tokens = tmpString.split("\t");

				String entity = URLDecoder.decode(tokens[0], "UTF-8").replaceAll("_", " ");

				if(dictionaryManager.getEntity2Id().containsKey(entity)){
					Film film = gson.fromJson(tokens[1], Film.class);

					entity2plot.put(dictionaryManager.getEntity2Id().get(entity), film.getPlot());
					entity2image.put(dictionaryManager.getEntity2Id().get(entity), film.getPoster());
					entity2rating.put(dictionaryManager.getEntity2Id().get(entity), film.getImdbRating());
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Error: load entity description!");
			e.printStackTrace();
		}
	}

	public Map<Integer, String> getEntity2Plot() {
		return entity2plot;
	}

	public Map<Integer, String> getEntity2Image() {
		return entity2image;
	}

	public Map<Integer, String> getEntity2Rating() {
		return entity2rating;
	}
}
