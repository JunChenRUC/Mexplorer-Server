/*
 * Copyright (c) 2018. by Chen Jun
 */

package cn.edu.ruc.domain;

public class Task {
    private int id;
    private String content;
    private int size;

    public Task(int id, String content, int size) {
        setId(id);
        setContent(content);
        setSize(size);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", size=" + size +
                '}';
    }
}
