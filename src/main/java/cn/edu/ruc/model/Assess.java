/*
 * Copyright (c) 2018. by Chen Jun
 */

package cn.edu.ruc.model;

import cn.edu.ruc.domain.Task;

import java.util.List;

public class Assess {
    private List<Task> taskList;
    private List<Integer> versionList;

    public Assess(List<Task> taskList, List<Integer> versionList) {
        setTaskList(taskList);
        setVersionList(versionList);
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public List<Integer> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<Integer> versionList) {
        this.versionList = versionList;
    }

    @Override
    public String toString() {
        return "Assess{" +
                "taskList=" + taskList +
                ", versionList=" + versionList +
                '}';
    }
}
