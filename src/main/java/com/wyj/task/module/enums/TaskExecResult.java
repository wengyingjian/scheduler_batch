package com.wyj.task.module.enums;

public enum TaskExecResult {
    /**
     * 分片任务全部完成
     */
    SUCCESS,
    /**
     * 分片任务出错，需要终止
     */
    STOP,
    /**
     * 分片任务未完成，需要再次调度
     */
    RETRY;
}
