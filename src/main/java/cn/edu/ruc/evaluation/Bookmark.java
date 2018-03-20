/*
 * Copyright (c) 2018. by Chen Jun
 */

package cn.edu.ruc.evaluation;


public class Bookmark {
    private String userId;
    private int taskId;
    private int versionId;
    private String name;
    private int rank;
    private int time;
    private String timestamp;
    private String queryContent;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getQueryContent() {
        return queryContent;
    }

    public void setQueryContent(String queryContent) {
        this.queryContent = queryContent;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "userId='" + userId + '\'' +
                ", taskId=" + taskId +
                ", versionId=" + versionId +
                ", name='" + name + '\'' +
                ", rank=" + rank +
                ", time=" + time +
                ", timestamp=" + timestamp +
                ", queryContent='" + queryContent + '\'' +
                '}';
    }
}
