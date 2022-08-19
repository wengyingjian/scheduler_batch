package com.wyj.task.repository.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TaskSplitPO {
    private Long id;
    private Long taskId;
    private Integer status;
    private Integer taskType;
    private Date createTime;
    private Date updateTime;
    private Date taskTime;
    private String bizData;
    private Long execCount;

}
