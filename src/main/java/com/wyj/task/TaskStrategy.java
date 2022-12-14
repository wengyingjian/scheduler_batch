package com.wyj.task;

import com.wyj.task.module.enums.TaskTypeEnum;
import com.wyj.task.module.Task;

/**
 * 任务策略
 * 业务系统实现
 * 由框架调用
 */
public interface TaskStrategy {

    /**
     * 获取任务类型
     */
    TaskTypeEnum getTaskType();

    /**
     * 任务分片策略
     */
    JobShardingStrategy shardingStrategy();

    /**
     * 任务处理逻辑，需要做好幂等处理
     */
    TaskHandler taskHandler();

}
