package com.wyj.task;

import com.wyj.task.module.enums.TaskExecResult;
import com.wyj.task.module.TaskSplit;

public interface TaskHandler {

    /**
     * 可以一次性执行完所有任务，返回成功
     * 或者一次处理部分任务，返回继续（但是需要内部记录状态）
     * <p>
     * 业务系统需要做逻辑的幂等处理，同一个任务，因为网络问题可能会重复执行
     * 框架会尽量保证任务不丢，但无法保证任务不重复
     * <p/>
     * mq默认超时时间为15min，若单次任务执行超过15min，则判断为任务失败
     *
     * @param split
     * @return
     */
    TaskExecResult execute(TaskSplit split);
}
