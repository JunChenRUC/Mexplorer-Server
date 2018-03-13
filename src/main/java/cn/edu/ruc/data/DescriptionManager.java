package cn.edu.ruc.data;

import cn.edu.ruc.domain.Film;
import com.google.gson.Gson;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DescriptionManager {
	public Map<Integer, String> id2plot = new HashMap<>();
	public Map<Integer, String> id2image = new HashMap<>();
	public Map<Integer, String> id2rating = new HashMap<>();
	public Set<Integer> idSet = new HashSet<>();

	public DescriptionManager(String inputPath, DictionaryManager dictionaryManager){
		loadEntity2Description(inputPath, dictionaryManager);
	}

	public void loadEntity2Description(String inputPath, DictionaryManager dictionaryManager){
		try {
			Gson gson = new Gson();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
			String tmpString;
			while((tmpString = reader.readLine()) != null) {
				String[] tokens = tmpString.split("\t");

				String entity = URLDecoder.decode(tokens[0], "UTF-8").replaceAll("_", " ");

				if(dictionaryManager.getSource2Id().containsKey(entity)){
					Film film = gson.fromJson(tokens[1], Film.class);

					if(film.getPoster() != null && film.getPoster().startsWith("http") && film.getPlot() != null && film.getPlot().length() > 5) {
						id2plot.put(dictionaryManager.getSource2Id().get(entity), film.getPlot());
						id2image.put(dictionaryManager.getSource2Id().get(entity), film.getPoster());
						id2rating.put(dictionaryManager.getSource2Id().get(entity), film.getImdbRating());
						idSet.add(dictionaryManager.getSource2Id().get(entity));
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Error: load entity description!");
			e.printStackTrace();
		}
	}

	public Map<Integer, String> getId2Plot() {
		return id2plot;
	}

	public Map<Integer, String> getId2Image() {
		return id2image;
	}

	public Map<Integer, String> getId2Rating() {
		return id2rating;
	}

	public Set<Integer> getIdSet() {
		return idSet;
	}
}
