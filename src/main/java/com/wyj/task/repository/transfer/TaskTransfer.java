package com.wyj.task.repository.transfer;

import com.wyj.task.module.enums.TaskStatusEnum;
import com.wyj.task.module.Task;
import com.wyj.task.module.TaskSplit;
import com.wyj.task.repository.entity.TaskPO;
import com.wyj.task.repository.entity.TaskSplitPO;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TaskTransfer {

    public static Task tranfer(TaskPO taskPO) {
        Task task = new Task();
        task.setId(taskPO.getId());
        task.setStatus(taskPO.getStatus());
        task.setTaskType(taskPO.getTaskType());
        task.setCreateTime(taskPO.getCreateTime());
        task.setUpdateTime(taskPO.getUpdateTime());
        task.setTaskTime(taskPO.getTaskTime());
        task.setBizData(taskPO.getBizData());
        return task;
    }

    public static  TaskPO tranfer(Task task) {
        TaskPO taskPO = new TaskPO();
        taskPO.setStatus(task.getStatus());
        taskPO.setTaskType(task.getTaskType());
        taskPO.setCreateTime(task.getCreateTime());
        taskPO.setUpdateTime(task.getUpdateTime());
        taskPO.setTaskTime(task.getTaskTime());
        taskPO.setBizData(task.getBizData());
        return taskPO;
    }

    public static  Task init(Integer type, Date taskTime, String bizData) {
        Task task = new Task();
        task.setStatus(TaskStatusEnum.INIT.getStatus());
        task.setTaskType(type);
        task.setTaskTime(taskTime);
        task.setBizData(bizData);
        return task;
    }

    public static TaskSplit initSplit(String bizData, Task task) {
        TaskSplit taskSplit = new TaskSplit();
        taskSplit.setStatus(TaskStatusEnum.INIT.getStatus());
        taskSplit.setTaskType(task.getTaskType());
        taskSplit.setTaskTime(task.getTaskTime());
        taskSplit.setBizData(bizData);
        return taskSplit;
    }

    public static  List<TaskSplitPO> tranfer(TaskPO task, List<TaskSplit> taskSplit) {
        return taskSplit.stream().map(fTaskSplit -> tranfer(task, fTaskSplit)).collect(Collectors.toList());
    }

    public static  TaskSplitPO tranfer(TaskPO task, TaskSplit taskSplit) {
        TaskSplitPO splitPO = new TaskSplitPO();
        splitPO.setTaskId(task.getId());
        splitPO.setCreateTime(task.getCreateTime());
        splitPO.setUpdateTime(task.getUpdateTime());
        splitPO.setTaskTime(task.getTaskTime());
        splitPO.setTaskType(task.getTaskType());

        splitPO.setStatus(taskSplit.getStatus());
        splitPO.setBizData(taskSplit.getBizData());
        return splitPO;
    }

    public static List<TaskSplit> tranferSplitList(List<TaskSplitPO> splitPO) {
        return splitPO.stream().map(TaskTransfer::transferSplit).collect(Collectors.toList());
    }

    public static TaskSplit transferSplit(TaskSplitPO splitPO) {
        TaskSplit taskSplit = new TaskSplit();
        taskSplit.setId(splitPO.getId());
        taskSplit.setTaskId(splitPO.getTaskId());
        taskSplit.setStatus(splitPO.getStatus());
        taskSplit.setTaskType(splitPO.getTaskType());
        taskSplit.setCreateTime(splitPO.getCreateTime());
        taskSplit.setUpdateTime(splitPO.getUpdateTime());
        taskSplit.setTaskTime(splitPO.getTaskTime());
        taskSplit.setExec_count(splitPO.getExecCount());
        taskSplit.setBizData(splitPO.getBizData());
        return taskSplit;
    }
}
