package com.wyj.task.core;

import org.apache.rocketmq.client.apis.ClientException;

public interface TaskService {
    void dispatch(TaskScheduler.StopChecker checker) throws ClientException;

    void scan(TaskScheduler.StopChecker checker);
}
