package cn.edu.ruc.core;

import cn.edu.ruc.data.*;
import cn.edu.ruc.domain.Task;
import org.apache.lucene.index.DirectoryReader;

import javax.servlet.http.HttpServlet;
import java.io.*;
import java.util.*;

public class DataUtil extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static int Dimension;
	public static int Output_Auto_Size;
	public static int Output_Entity_Size;
	public static int Output_Relation_Size;
	public static int Output_Feature_Size;
	public static int[] Directions;

	public static int Task_Size;
	public static int Version_Size;

	private static ConfigManager configManager;
	private static LogManager logManager;
	private static DictionaryManager dictionaryManager;
	private static TripleManager tripleManager;
	private static VectorManager vectorManager;
	private static IndexManager indexManager;
	private static DescriptionManager descriptionManager;
	private static TaskManager taskManager;

	public DataUtil(){
		System.out.println("------------------------------------------------------");

		loadConfiguration();
		loadParameter();
		loadData();

		System.out.println("------------------------------------------------------");
	}

	private void loadConfiguration(){
		configManager = new ConfigManager("conf.properties");
		logManager = new LogManager();
		System.out.println("Configurations are loaded!");
	}

	private void loadParameter(){
		Dimension = Integer.parseInt(configManager.getValue("dimension"));
		Output_Auto_Size = Integer.parseInt(configManager.getValue("output.auto.size"));
		Output_Entity_Size = Integer.parseInt(configManager.getValue("output.entity.size"));
		Output_Relation_Size = Integer.parseInt(configManager.getValue("output.relation.size"));
		Output_Feature_Size = Integer.parseInt(configManager.getValue("output.feature.size"));
		Directions = new int[]{Integer.parseInt(configManager.getValue("direction.forward")), Integer.parseInt(configManager.getValue("direction.backward"))};

		Task_Size = Integer.parseInt(configManager.getValue("task.size"));
		Version_Size = Integer.parseInt(configManager.getValue("version.size"));
		System.out.println("Parameters are loaded!");
	}

	private void loadData(){
		long beginTime;

		beginTime = System.currentTimeMillis();
		dictionaryManager = new DictionaryManager(configManager.getValue("dir") + configManager.getValue("file.dictionary.entity"), configManager.getValue("dir") + configManager.getValue("file.dictionary.relation"));
		System.out.println("Dictionaries are loaded! Time cost: " + (System.currentTimeMillis() - beginTime)/1000 );

		beginTime = System.currentTimeMillis();
		tripleManager = new TripleManager(configManager.getValue("dir") + configManager.getValue("file.triple"), dictionaryManager);
		System.out.println("Triples are loaded! Time cost: " + (System.currentTimeMillis() - beginTime)/1000 );

		beginTime = System.currentTimeMillis();
		vectorManager = new VectorManager(configManager.getValue("dir") + configManager.getValue("model") + configManager.getValue("file.vector.entity"), configManager.getValue("dir") + configManager.getValue("model") + configManager.getValue("file.vector.relation"),  configManager.getValue("dimension"), dictionaryManager);
		System.out.println("Vectors are loaded! Time cost: " + (System.currentTimeMillis() - beginTime)/1000 );

		beginTime = System.currentTimeMillis();
		indexManager = new IndexManager(configManager.getValue("dir") + configManager.getValue("file.index"), configManager.getValue("dir") + configManager.getValue("index"));
		System.out.println("Indexes are loaded! Time cost: " + (System.currentTimeMillis() - beginTime)/1000 );

		beginTime = System.currentTimeMillis();
		descriptionManager = new DescriptionManager(configManager.getValue("dir") + configManager.getValue("file.description"), dictionaryManager);
		System.out.println("Description are loaded! Time cost: " + (System.currentTimeMillis() - beginTime)/1000 );

		beginTime = System.currentTimeMillis();
		taskManager = new TaskManager(configManager.getValue("dir") + configManager.getValue("file.task"));
		System.out.println("Tasks are loaded! Time cost: " + (System.currentTimeMillis() - beginTime)/1000 );
	}

	public static LogManager getLogManager () {
		return logManager;
	}

	public static String getId2Source(int id){
		return dictionaryManager.getId2Source().get(id);
	}

	public static int getSource2Id(String name){
		return dictionaryManager.getSource2Id().containsKey(name) ? dictionaryManager.getSource2Id().get(name) : -1;
	}

	public static String getId2Target(int id){
		return dictionaryManager.getId2Target().get(id);
	}

	public static int getTarget2Id(String name){
		return dictionaryManager.getTarget2Id().containsKey(name) ? dictionaryManager.getTarget2Id().get(name) : -1;
	}

	public static String getId2Relation(int id){
		return dictionaryManager.getId2Relation().get(id);
	}

	public static int getRelation2Id(String name){
		return dictionaryManager.getRelation2Id().containsKey(name) ? dictionaryManager.getRelation2Id().get(name) : -1;
	}

	public static double[] getEntity2Vector(int id){
		return vectorManager.getEntity2Vector().get(id);
	}

	public static double[] getRelation2Vector(int id){
		return vectorManager.getRelation2Vector().get(id);
	}

	public static String getPlot(int id) {
		return descriptionManager.getId2Plot().containsKey(id) ? descriptionManager.getId2Plot().get(id) : "";
	}

	public static String getImage(int id) {
		return descriptionManager.getId2Image().containsKey(id) ? descriptionManager.getId2Image().get(id) : "";
	}

	public static String getRating(int id) {
		return descriptionManager.getId2Rating().containsKey(id) ? descriptionManager.getId2Rating().get(id) : "";
	}

	public static boolean hasDescription(int id) {
		return descriptionManager.getIdSet().contains(id) ? true : false;
	}

	public static DirectoryReader getDirectoryReader(){
		return indexManager.getDirectoryReader();
	}

	public static Set<Integer> getWholeSourceIdSet() {
		return dictionaryManager.getId2Source().keySet();
	}

	public static Set<Integer> getWholeTargetIdSet() {
		return dictionaryManager.getId2Target().keySet();
	}

	public static Set<Integer> getSourceIdSet(int queryEntityId, int queryRelationId, int queryRelationDirection) {
		Set<Integer> entitySet = new HashSet<>();

		if(tripleManager.getDirection2TripleMap().get(queryRelationDirection).containsKey(queryEntityId)) {
			if(tripleManager.getDirection2TripleMap().get(queryRelationDirection).get(queryEntityId).containsKey(queryRelationId)) {
				entitySet = tripleManager.getDirection2TripleMap().get(queryRelationDirection).get(queryEntityId).get(queryRelationId);
			}
		}

		return entitySet;
	}

	public static Set<Integer> getTargetIdSet(int queryEntityId, int queryRelationId, int queryRelationDirection) {
		Set<Integer> entitySet = new HashSet<>();

		if(tripleManager.getDirection2TripleMap().get(queryRelationDirection).containsKey(queryEntityId)) {
			if(tripleManager.getDirection2TripleMap().get(queryRelationDirection).get(queryEntityId).containsKey(queryRelationId)) {
				entitySet = tripleManager.getDirection2TripleMap().get(queryRelationDirection).get(queryEntityId).get(queryRelationId);
			}
		}

		return entitySet;
	}

	public static Set<Integer> getSourceIdSet(int queryEntityId) {
		Set<Integer> entitySet = new HashSet<>();

		for(int direction : Directions) {
			for(Map.Entry<Integer, Set<Integer>> relation2entityEntry : getRelation2TargetMap(queryEntityId, direction).entrySet()) {
				for(int targetEntityId : relation2entityEntry.getValue()) {
					entitySet.addAll(getTargetIdSet(targetEntityId, relation2entityEntry.getKey(), - direction));
				}
			}
		}

		return entitySet;
	}

	public static Map<Integer, Set<Integer>> getRelation2TargetMap(int queryEntityId, int direction) {
		Map<Integer, Set<Integer>> relation2entityMap = new HashMap<>();

		if(DataUtil.tripleManager.getDirection2TripleMap().get(direction).containsKey(queryEntityId)) {
			relation2entityMap = DataUtil.tripleManager.getDirection2TripleMap().get(direction).get(queryEntityId);
		}

		return relation2entityMap;
	}

	public static Task getTask(int id) {
		return taskManager.getTaskMap().get(id);
	}

	public static int getUserId() {
		int userId = 0;
		try {
			File file = new File(configManager.getValue("dir") + configManager.getValue("dir.log") + configManager.getValue("file.user.log"));
			if(!file.exists()) {
				new File(configManager.getValue("dir") + configManager.getValue("dir.log")).mkdirs();
				file.createNewFile();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String tmpString;
			while((tmpString = reader.readLine()) != null) {
				userId ++;
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Error: load entity description!");
			e.printStackTrace();
		}

		return userId;
	}

	public static void writeUser(String userId) {
		try {
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(configManager.getValue("dir") + configManager.getValue("dir.log")  + configManager.getValue("file.user.log"), true)));

			printWriter.println(userId);

			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeBookmark(String userId, int taskId, int versionId, List<String> relevantEntityStringList) {
		try {
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(configManager.getValue("dir") + configManager.getValue("dir.log")  + configManager.getValue("file.bookmark.log"), true)));

			for(int j = 0 ; j < relevantEntityStringList.size(); j ++) {
				printWriter.println(userId + "\t" + taskId + "\t" + versionId + "\t" + relevantEntityStringList.get(j));
			}

			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeInteraction(String userId, int taskId, int versionId, String area, String option, String content, String timestamp) {
		try {
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(configManager.getValue("dir") + configManager.getValue("dir.log")  + configManager.getValue("file.interaction.log"), true)));

			printWriter.println(userId + "\t" + taskId + "\t" + versionId + "\t" + area + "\t" + option + "\t" + content + "\t" + timestamp + "\t");

			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
