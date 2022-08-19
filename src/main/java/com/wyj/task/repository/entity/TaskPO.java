package com.wyj.task.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TaskPO {
    private Long id;
    private Integer status;
    private Integer taskType;
    private Date createTime;
    private Date updateTime;
    private Date taskTime;
}
