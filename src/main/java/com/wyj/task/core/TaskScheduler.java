package com.wyj.task.core;

public class TaskScheduler {

    private TaskService taskService;

    //cron 1分钟
    public void schedule() {
        //分发任务
        taskService.map();
        //扫描任务是否完成
        taskService.reduce();
    }


}
