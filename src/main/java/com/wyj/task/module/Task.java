package com.wyj.task.module;

import com.wyj.task.repository.transfer.TaskTransfer;
import lombok.Data;

import java.util.Date;

@Data
public class Task {
    private Long id;
    private Integer status;
    private Integer taskType;
    private Date createTime;
    private Date updateTime;
    private Date taskTime;
    /**
     * 不进行持久化
     */
    private String bizData;

    public static Task init(Integer taskType, Date taskTime, String bizData) {
        return TaskTransfer.init(taskType, taskTime, bizData);
    }

}
