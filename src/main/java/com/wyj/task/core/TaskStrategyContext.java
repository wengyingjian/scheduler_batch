package com.wyj.task.core;

import com.wyj.task.TaskStrategy;
import com.wyj.task.module.enums.TaskTypeEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("taskStrategyContext")
public class TaskStrategyContext {
    @Resource
    private List<TaskStrategy> taskStrategyList;

    private static Map<Integer, TaskStrategy> taskStrategyMap;

    @PostConstruct
    public void init() {
        taskStrategyMap = taskStrategyList.stream().collect(Collectors.toMap(
                taskServiceTemplate -> taskServiceTemplate.getTaskType().getType(),
                Function.identity()
        ));
    }

    public static TaskStrategy getTask(Integer taskType) {
        return taskStrategyMap.get(taskType);
    }

    public static List<TaskStrategy> getTasks() {
        return new ArrayList<>(taskStrategyMap.values());
    }

    public static String getTaskTopic(Integer taskType) {
        String suffix = getTask(taskType).getTaskType().toString();
        return "task_topic_" + suffix;
    }

    public static String getTaskConsumerGroup(Integer taskType) {
        String suffix = getTask(taskType).getTaskType().toString();
        return "task_consumer_group_" + suffix;
    }


    public static TaskStrategy getTask(TaskTypeEnum taskType) {
        return taskStrategyMap.get(taskType.getType());
    }
}
