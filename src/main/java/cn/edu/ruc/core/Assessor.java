/*
 * Copyright (c) 2018. by Chen Jun
 */

package cn.edu.ruc.core;

import cn.edu.ruc.domain.Task;

import java.util.ArrayList;
import java.util.List;

public class Assessor {
    public static List<Task> getTaskList(int id) {
        List<Task> taskList = new ArrayList<>();
        for(int i = 0; i < DataUtil.Task_Size; i ++) {
            taskList.add(DataUtil.getTask((id + i) % DataUtil.Task_Size + 1));
        }

        return taskList;
    }

    public static List<Integer> getVersionList(int id) {
        List<Integer> versionList = new ArrayList<>();
        for(int i = 0; i < DataUtil.Version_Size; i ++) {
            versionList.add((id / DataUtil.Task_Size + i) % DataUtil.Version_Size + 1);
        }

        return versionList;
    }
}
