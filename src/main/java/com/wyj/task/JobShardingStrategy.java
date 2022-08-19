package com.wyj.task;

import com.wyj.task.module.Task;
import com.wyj.task.module.TaskSplit;

import java.util.List;

public interface JobShardingStrategy<T> {

    List<TaskSplit<T>> sharding(Task<T> task);
}
