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

	public static String getId2Entity(int id){
		return dictionaryManager.getId2Entity().get(id);
	}

	public static int getEntity2Id(String name){
		return dictionaryManager.getEntity2Id().containsKey(name) ? dictionaryManager.getEntity2Id().get(name) : -1;
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

	public static String getEntity2Plot(int id) {
		return descriptionManager.getEntity2Plot().containsKey(id) ? descriptionManager.getEntity2Plot().get(id) : "";
	}

	public static String getEntity2Image(int id) {
		return descriptionManager.getEntity2Image().containsKey(id) ? descriptionManager.getEntity2Image().get(id) : "";
	}

	public static String getEntity2Rating(int id) {
		return descriptionManager.getEntity2Rating().containsKey(id) ? descriptionManager.getEntity2Rating().get(id) : "";
	}

	public static DirectoryReader getDirectoryReader(){
		return indexManager.getDirectoryReader();
	}

	public static Task getTask(int id) {
		return taskManager.getTaskMap().get(id);
	}

	public static Map<Integer, Task> getTaskMap() {
		return taskManager.getTaskMap();
	}

	public static List<Integer> getWholeSourceEntityIdList() {
		return dictionaryManager.getSourceEntityIdList();
	}

	public static List<Integer> getWholeTargetEntityIdList() {
		return dictionaryManager.getTargetEntityIdList();
	}

	public static Set<Integer> getTargetEntityIdSet(int queryEntityId, int queryRelationId, int queryRelationDirection) {
		Set<Integer> entitySet = new HashSet<>();

		if(tripleManager.getDirection2TripleMap().get(queryRelationDirection).containsKey(queryEntityId)) {
			if(tripleManager.getDirection2TripleMap().get(queryRelationDirection).get(queryEntityId).containsKey(queryRelationId)) {
				entitySet = tripleManager.getDirection2TripleMap().get(queryRelationDirection).get(queryEntityId).get(queryRelationId);
			}
		}

		return entitySet;
	}

	public static Set<Integer> getSourceEntityIdSet(int queryEntityId) {
		Set<Integer> entitySet = new HashSet<>();

		for(int direction : Directions) {
			for(Map.Entry<Integer, Set<Integer>> relation2entityEntry : getRelation2EntityMap(queryEntityId, direction).entrySet()) {
				for(int targetEntityId : relation2entityEntry.getValue()) {
					entitySet.addAll(getTargetEntityIdSet(targetEntityId, relation2entityEntry.getKey(), - direction));
				}
			}
		}

		return entitySet;
	}

	public static Map<Integer, Set<Integer>> getRelation2EntityMap(int queryEntityId, int direction) {
		Map<Integer, Set<Integer>> relation2entityMap = new HashMap<>();

		if(DataUtil.tripleManager.getDirection2TripleMap().get(direction).containsKey(queryEntityId)) {
			relation2entityMap = DataUtil.tripleManager.getDirection2TripleMap().get(direction).get(queryEntityId);
		}

		return relation2entityMap;
	}

	public static void writeBookmark(String userId, String taskId, String versionId, List<String> relevantEntityStringList) {
		try {
			File file = new File(configManager.getValue("dir") + configManager.getValue("file.bookmark.log"));
			if(!file.exists())
				file.createNewFile();

			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

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

	public static void writeInteraction(String userId, String taskId, String versionId, String area, String option, String content, String timestamp) {
		try {
			File file = new File(configManager.getValue("dir") + configManager.getValue("file.interaction.log"));
			if(!file.exists())
				file.createNewFile();

			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

			printWriter.println(userId + "\t" + taskId + "\t" + versionId + "\t" + area + "\t" + option + "\t" + content + "\t" + timestamp + "\t");

			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
