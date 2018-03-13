/*
 * Copyright (c) 2018. by Chen Jun
 */

package cn.edu.ruc.domain;

public class Task {
    private int id;
    private String description;
    private String content;
    private int size;
    private int versionId;

    public Task(int id, String description, String content, int size) {
        setId(id);
        setDescription(description);
        setContent(content);
        setSize(size);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", content='" + content + '\'' +
                ", size=" + size +
                ", versionId=" + versionId +
                '}';
    }
}
