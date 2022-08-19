package com.wyj.task.core;

import com.wyj.task.TaskStrategy;
import com.wyj.task.module.enums.TaskTypeEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TaskStrategyContext implements InitializingBean {
    @Resource
    private List<TaskStrategy> taskStrategyList;

    private static Map<Integer, TaskStrategy> taskStrategyMap;

    @Override
    public void afterPropertiesSet() {
        taskStrategyMap = taskStrategyList.stream().collect(Collectors.toMap(
                taskServiceTemplate -> taskServiceTemplate.getTaskType().getType(),
                Function.identity()
        ));
    }

    public static TaskStrategy getTask(Integer taskType) {
        return taskStrategyMap.get(taskType);
    }

    public static TaskStrategy getTask(TaskTypeEnum taskType) {
        return taskStrategyMap.get(taskType.getType());
    }
}
