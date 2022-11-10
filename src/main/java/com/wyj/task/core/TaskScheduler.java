package com.wyj.task.core;

import org.apache.rocketmq.client.apis.ClientException;

public class TaskScheduler {

    private TaskService taskService;

    //cron 1分钟
    public void schedule() throws ClientException {
        long start = System.currentTimeMillis();
        long gap = (60 - 5) * 1000L;//任务1分钟调度一次，每次任务执行55秒，
        StopChecker checker = new StopChecker(start + gap);

        //分发任务
        taskService.dispatch(checker);
        //扫描任务是否完成
        taskService.scan(checker);
    }


    public static class StopChecker {
        long deadLine;

        public StopChecker(long deadLine) {
            this.deadLine = deadLine;
        }

        /***
         * 检查是否要中止任务
         * 返回true代表需要中止
         */
        boolean check() {
            if (deadLine == -1) {
                return false;
            }
            return System.currentTimeMillis() >= deadLine;
        }

    }

}
