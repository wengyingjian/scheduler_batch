package com.wyj.task.core;

import com.wyj.task.core.TaskService;

public class TaskScheduler {

    private TaskService taskService;

    //cron 1分钟
    public void schedule() {
        //分发任务
        taskService.dispatch();
        //扫描任务是否完成
        taskService.finalization();
    }


}
