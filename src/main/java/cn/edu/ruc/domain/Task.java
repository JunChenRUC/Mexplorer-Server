/*
 * Copyright (c) 2018. by Chen Jun
 */

package cn.edu.ruc.domain;

public class Task {
    private String content;
    private int size;

    public Task(String content, int size) {
        setContent(content);
        setSize(size);
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
                "content='" + content + '\'' +
                ", size=" + size +
                '}';
    }
}
