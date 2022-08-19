package com.wyj.task.module.enums;

public enum TaskSplitStatusEnum {
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
    STOP(3);
    int status;

    public static TaskSplitStatusEnum valueOf(Integer status) {
        if (status == null) {
            throw new RuntimeException();
        }
        for (TaskSplitStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus() == status) {
                return statusEnum;
            }
        }
        throw new RuntimeException("status not supported");
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    TaskSplitStatusEnum(int status) {
        this.status = status;
    }
}
