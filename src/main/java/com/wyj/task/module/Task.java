package com.wyj.task.module;

import java.util.Date;

public class Task<T> {
    private Long id;
    private Integer status;
    private Integer taskType;
    private Date createTime;
    private Date updateTime;
    private Date taskTime;
    /**
     * 不进行持久化
     */
    private T bizData;

    public T getBizData() {
        return bizData;
    }

    public void setBizData(T bizData) {
        this.bizData = bizData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(Date taskTime) {
        this.taskTime = taskTime;
    }
}
