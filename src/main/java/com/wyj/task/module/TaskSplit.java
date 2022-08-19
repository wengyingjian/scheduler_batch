package com.wyj.task.module;

import com.wyj.task.repository.transfer.TaskTransfer;
import lombok.Data;

import java.util.Date;

@Data
public class TaskSplit {
    private Long id;
    private Long taskId;
    private Integer status;
    private Integer taskType;
    private Date createTime;
    private Date updateTime;
    private Date taskTime;
    private Long exec_count;
    private String bizData;

    public static  TaskSplit init(String bizData, Task task) {
        return TaskTransfer.initSplit(bizData, task);
    }

}
