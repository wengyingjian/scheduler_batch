package com.wyj.task.module.enums;

public enum TaskStatusEnum {
    /**
     * 任务初始化
     */
    INIT(0),
    /**
     * 任务处理中：已分发->未完成
     */
    EXECUTING(1),
    /**
     * 任务完结：全部成功
     */
    SUCCESS(2),
    /**
     * 任务完结：部分失败
     */
    FINISH(3);

    int status;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    TaskStatusEnum(int status) {
        this.status = status;
    }
}
