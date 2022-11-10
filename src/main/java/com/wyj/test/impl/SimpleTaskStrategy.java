package com.wyj.test.impl;

import com.wyj.task.JobShardingStrategy;
import com.wyj.task.TaskHandler;
import com.wyj.task.module.enums.TaskExecResult;
import com.wyj.task.module.Task;
import com.wyj.task.module.TaskSplit;
import com.wyj.task.TaskStrategy;
import com.wyj.task.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SimpleTaskStrategy implements TaskStrategy {

    @Override
    public com.wyj.task.module.enums.TaskTypeEnum getTaskType() {
        return MyTaskTypeEnum.SIMPLE_FLUSH_CACHE;
    }

    @Override
    public JobShardingStrategy shardingStrategy() {
        return task -> {
            //提取task中的业务字段
            String strategyId = task.getBizData();
            List<String> storeIdList = queryStoreIdByStrategyIds(strategyId);

            //对task结合业务属性进行分片
            Map<Long, TaskSplit> splitMap = new HashMap<>();
            for (String storeId : storeIdList) {
                Long key = Long.parseLong(storeId) % 1000;

                TaskSplit taskSplit = splitMap.get(key);
                if (taskSplit == null) {
                    taskSplit = TaskSplit.init(JsonUtil.obj2String(SimpleCache.init(strategyId)), task);
                    splitMap.put(key, taskSplit);
                }
                SimpleCache strategy = JsonUtil.string2Obj(taskSplit.getBizData(), SimpleCache.class);
                taskSplit.setBizData(JsonUtil.obj2String(strategy));
            }

            return new ArrayList<>(splitMap.values());
        };
    }


    //mock，此处模拟数据库查询
    //为了减少没用的分片(按照门店id分片），此处进行数据库查询，根据关联的门店id，仅分发有任务的分片
    private List<String> queryStoreIdByStrategyIds(String strategyId) {
        List<String> storeIds = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            storeIds.add(strategyId + new Random().nextInt(10000));
        }
        return storeIds;
    }

    @Override
    public TaskHandler taskHandler() {
        return new TaskHandler() {
            @Override
            public TaskExecResult execute(TaskSplit split) {
                //1.获取当前任务所有门店
                //2.处理门店数据
                //3.返回结果
                //mock
                int random = new Random().nextInt(4);
                if (random == 0) {
                    return TaskExecResult.SUCCESS;
                }
                if (random == 1) {
                    return TaskExecResult.STOP;
                }
                if (random == 2) {
                    return TaskExecResult.RETRY;
                }
                //timeout
                try {
                    Thread.sleep(3000 * 60);
                    return TaskExecResult.SUCCESS;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void reduce(Task task) {
                System.out.println("finalize:" + JsonUtil.obj2String(task));
            }
        };
    }


}
