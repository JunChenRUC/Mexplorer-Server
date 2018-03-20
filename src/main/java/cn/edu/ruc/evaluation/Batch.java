/*
 * Copyright (c) 2018. by Chen Jun
 */

package cn.edu.ruc.evaluation;

import cn.edu.ruc.data.ConfigManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Batch {
    private static Set<String> userSet = new HashSet<>();
    private static Map<Integer, Map<Integer, Map<String, List<Bookmark>>>> version2task2user2bookmarkListMap = new HashMap<>();
    private static Map<Integer, Map<Integer, Map<String, List<Interaction>>>> version2task2user2interactionListMap = new HashMap<>();


    public static void main(String[] args) {
        ConfigManager configManager = new ConfigManager("conf.properties");

        loadUser(configManager.getValue("dir") + configManager.getValue("dir.log") + configManager.getValue("file.user.log"));

        System.out.println("User size: " + userSet.size());

        loadBookmark(configManager.getValue("dir") + configManager.getValue("dir.log") + configManager.getValue("file.bookmark.log"));

        statisticBookmark();

        loadInteraction(configManager.getValue("dir") + configManager.getValue("dir.log") + configManager.getValue("file.interaction.log"));

        statisticInteraction();
    }

    public static void statisticBookmark() {
        System.out.println("Bookmark statistics: ");
        for(Map.Entry<Integer, Map<Integer, Map<String, List<Bookmark>>>> version2task2user2bookmarkListEntry : version2task2user2bookmarkListMap.entrySet()) {
            int versionId = version2task2user2bookmarkListEntry.getKey();
            for(Map.Entry<Integer, Map<String, List<Bookmark>>> task2user2bookmarkListEntry : version2task2user2bookmarkListEntry.getValue().entrySet()) {
                int taskId = task2user2bookmarkListEntry.getKey();
                int bookmark_time_sum = 0, query_fre_sum = 0, rank_sum = 0, user_size = task2user2bookmarkListEntry.getValue().keySet().size();
                for(Map.Entry<String, List<Bookmark>> user2bookmarkListEntry : task2user2bookmarkListEntry.getValue().entrySet()) {
                    List<Bookmark> bookmarkList = user2bookmarkListEntry.getValue();
                    int bookmark_time = 0;
                    Set<String> querySet = new HashSet<>();
                    for (Bookmark bookmark : bookmarkList) {
                        bookmark_time += bookmark.getTime();
                        rank_sum += bookmark.getRank();
                        querySet.add(bookmark.getQueryContent());
                    }
                    rank_sum = rank_sum / bookmarkList.size();
                    bookmark_time_sum += bookmark_time / bookmarkList.size();
                    query_fre_sum += querySet.size();
                }
                System.out.println(versionId + "\t" + taskId + "\tmean bookmark time: " + (double) bookmark_time_sum / user_size / 1000 + "\tmean query fre: " + (double) query_fre_sum / user_size + "\tmean rank: " + (double) rank_sum / user_size);
            }
        }
    }

    public static void statisticInteraction() {
        System.out.println("Interaction statistics: ");
        for(Map.Entry<Integer, Map<Integer, Map<String, List<Interaction>>>> version2task2user2interactionListEntry : version2task2user2interactionListMap.entrySet()) {
            int versionId = version2task2user2interactionListEntry.getKey();
            for(Map.Entry<Integer, Map<String, List<Interaction>>> task2user2interactionListEntry : version2task2user2interactionListEntry.getValue().entrySet()) {
                int taskId = task2user2interactionListEntry.getKey();
                int query_fre_sum = 0, movie_lookup_fre_sum = 0, movie_add_fre_sum = 0, profile_feature_add_fre_sum = 0, left_feature_add_fre_sum= 0, right_feature_add_fre_sum = 0, task_time_sum = 0, user_size = task2user2interactionListEntry.getValue().keySet().size();
                for(Map.Entry<String, List<Interaction>> user2interactionListEntry : task2user2interactionListEntry.getValue().entrySet()) {
                    List<Interaction> interactionList = user2interactionListEntry.getValue();
                    for(Interaction interaction : interactionList) {
                        if(interaction.getArea().equals("query")){
                            if(interaction.getOption().equals("search")) {
                                query_fre_sum ++;
                            }
                        }
                        else if(interaction.getArea().equals("movie")) {
                            if(interaction.getOption().equals("lookup entity")) {
                                movie_lookup_fre_sum ++;
                            }
                            else if(interaction.getOption().equals("add entity")) {
                                movie_add_fre_sum ++;
                            }
                        }
                        else if(interaction.getArea().equals("profile")) {
                            if(interaction.getOption().equals("add feature")) {
                                profile_feature_add_fre_sum ++;
                            }
                        }
                        else if(interaction.getArea().equals("left feature")) {
                            if(interaction.getOption().equals("add feature")) {
                                left_feature_add_fre_sum ++;
                            }
                        }
                        else if(interaction.getArea().equals("right feature")) {
                            if(interaction.getOption().equals("add feature")) {
                                right_feature_add_fre_sum ++;
                            }
                        }
                    }

                    task_time_sum += Long.parseLong(interactionList.get(interactionList.size() - 1).getTimestamp()) - Long.parseLong(interactionList.get(0).getTimestamp());

                    /*String userId = user2interactionListEntry.getKey();
                    Map<String, Map<String, Integer>> area2option2frequency = new HashMap<>();
                    for (Interaction interaction : interactionList) {
                        if(!area2option2frequency.containsKey(interaction.getArea()))
                            area2option2frequency.put(interaction.getArea(), new HashMap<>());

                        if(!area2option2frequency.get(interaction.getArea()).containsKey(interaction.getOption()))
                            area2option2frequency.get(interaction.getArea()).put(interaction.getOption(), 1);
                        else
                            area2option2frequency.get(interaction.getArea()).put(interaction.getOption(), area2option2frequency.get(interaction.getArea()).get(interaction.getOption()) + 1);
                    }

                    System.out.println("Frequency statistics:");
                    for(Map.Entry<String, Map<String, Integer>> area2option2frequencyEntry : area2option2frequency.entrySet()) {
                        for(Map.Entry<String, Integer> option2frequencyEntry : area2option2frequencyEntry.getValue().entrySet()) {
                            System.out.println(versionId + "\t" + taskId + "\t" + userId + "\t" + area2option2frequencyEntry.getKey() + "\t" + option2frequencyEntry.getKey() + "\t" + option2frequencyEntry.getValue());
                        }
                    }*/
                }
                System.out.println(versionId + "\t" + taskId + "\tmean query size: " + (double) query_fre_sum / user_size
                        + "\tmean movie lookup fre: " + (double) movie_lookup_fre_sum / user_size
                        + "\tmean movie add fre: " + (double) movie_add_fre_sum / user_size
                        + "\tmean profile feature add fre: " + (double) profile_feature_add_fre_sum / user_size
                        + "\tmean left feature add fre: " + (double) left_feature_add_fre_sum / user_size
                        + "\tmean right feature add fre: " + (double) right_feature_add_fre_sum / user_size
                        + "\tmean task time: " + (double) task_time_sum / user_size / 1000
                );
            }
        }
    }

    public static void loadUser(String inputPath){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
            String tmpString;
            while((tmpString = reader.readLine()) != null) {
               userSet.add(tmpString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadBookmark(String inputPath){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
            String tmpString;
            while((tmpString = reader.readLine()) != null) {
                String[] tokens = tmpString.split("\t");
                if(userSet.contains(tokens[0])) {
                    Bookmark bookmark = new Bookmark();
                    bookmark.setUserId(tokens[0]);
                    bookmark.setTaskId(Integer.parseInt(tokens[1]));
                    bookmark.setVersionId(Integer.parseInt(tokens[2]));

                    String[] tokens_result = tokens[3].split("__")[0].split("_");
                    bookmark.setName(tokens_result[0]);
                    bookmark.setRank(Integer.parseInt(tokens_result[1]));
                    bookmark.setTime(Integer.parseInt(tokens_result[2]));
                    bookmark.setTimestamp(tokens_result[3]);
                    bookmark.setQueryContent(tokens[3].split("__")[1]);

                    if(!version2task2user2bookmarkListMap.containsKey(bookmark.getVersionId()))
                        version2task2user2bookmarkListMap.put(bookmark.getVersionId(), new HashMap<>());
                    if(!version2task2user2bookmarkListMap.get(bookmark.getVersionId()).containsKey(bookmark.getTaskId()))
                        version2task2user2bookmarkListMap.get(bookmark.getVersionId()).put(bookmark.getTaskId(), new HashMap<>());
                    if(!version2task2user2bookmarkListMap.get(bookmark.getVersionId()).get(bookmark.getTaskId()).containsKey(bookmark.getUserId()))
                        version2task2user2bookmarkListMap.get(bookmark.getVersionId()).get(bookmark.getTaskId()).put(bookmark.getUserId(), new ArrayList<>());

                    version2task2user2bookmarkListMap.get(bookmark.getVersionId()).get(bookmark.getTaskId()).get(bookmark.getUserId()).add(bookmark);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadInteraction(String inputPath){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
            String tmpString;
            while((tmpString = reader.readLine()) != null) {
                String[] tokens = tmpString.split("\t");
                if(userSet.contains(tokens[0])) {
                    Interaction interaction = new Interaction();
                    interaction.setUserId(tokens[0]);
                    interaction.setTaskId(Integer.parseInt(tokens[1]));
                    interaction.setVersionId(Integer.parseInt(tokens[2]));
                    interaction.setArea(tokens[3]);
                    interaction.setOption(tokens[4]);
                    interaction.setContent(tokens[5]);
                    interaction.setTimestamp(tokens[6]);

                    if(!version2task2user2interactionListMap.containsKey(interaction.getVersionId()))
                        version2task2user2interactionListMap.put(interaction.getVersionId(), new HashMap<>());
                    if(!version2task2user2interactionListMap.get(interaction.getVersionId()).containsKey(interaction.getTaskId()))
                        version2task2user2interactionListMap.get(interaction.getVersionId()).put(interaction.getTaskId(), new HashMap<>());
                    if(!version2task2user2interactionListMap.get(interaction.getVersionId()).get(interaction.getTaskId()).containsKey(interaction.getUserId()))
                        version2task2user2interactionListMap.get(interaction.getVersionId()).get(interaction.getTaskId()).put(interaction.getUserId(), new ArrayList<>());

                    version2task2user2interactionListMap.get(interaction.getVersionId()).get(interaction.getTaskId()).get(interaction.getUserId()).add(interaction);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
