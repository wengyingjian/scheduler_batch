
package com.wyj.test.impl;

import com.wyj.task.JobShardingStrategy;
import com.wyj.task.TaskHandler;
import com.wyj.task.TaskStrategy;
import com.wyj.task.module.Task;
import com.wyj.task.module.TaskSplit;
import com.wyj.task.module.enums.TaskExecResult;
import com.wyj.task.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MultiTaskStrategy implements TaskStrategy {

    @Override
    public MyTaskTypeEnum getTaskType() {
        return MyTaskTypeEnum.MULTI_TASK_TEST;
    }

    @Override
    public JobShardingStrategy shardingStrategy() {
        return task -> {
            List<TaskSplit> splits = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                TaskSplit taskSplit = TaskSplit.init(i + "", task);
                splits.add(taskSplit);
            }
            return splits;
        };
    }

    private static final Map<String, Integer> splitRetryRecord = new ConcurrentHashMap<>();

    @Override
    public TaskHandler taskHandler() {
        return new TaskHandler() {
            @Override
            public TaskExecResult execute(TaskSplit split) {
                //mock
                splitRetryRecord.putIfAbsent(split.getBizData(), 1);
                //进行2000次重试后再返回成功
                int count = splitRetryRecord.get(split.getBizData());
                if (count <= 100) {
                    splitRetryRecord.put(split.getBizData(), count + 1);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return TaskExecResult.RETRY;
                }
                splitRetryRecord.remove(split.getBizData());
                return TaskExecResult.SUCCESS;

            }

            @Override
            public void reduce(Task task) {
                System.out.println("finalize:" + JsonUtil.obj2String(task));
            }
        };
    }


}
