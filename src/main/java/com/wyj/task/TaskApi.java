package com.wyj.task;

import com.wyj.task.module.enums.TaskTypeEnum;

import java.util.Date;

/**
 * 给业务系统暴露的接口
 */
public interface TaskApi {
    /**
     * 创建任务
     * 若使用周期任务，任务系统可借助schedule通过cron，手动创建任务
     *
     * @param taskType 任务类型
     * @param taskTime 任务时间
     * @param bizData  业务字段
     */
    void submit(TaskTypeEnum taskType, Date taskTime, String bizData);

}
