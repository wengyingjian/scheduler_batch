package com.wyj.task.repository;

import com.wyj.task.enums.TaskSplitStatusEnum;
import com.wyj.task.enums.TaskStatusEnum;
import com.wyj.task.module.Task;
import com.wyj.task.module.TaskSplit;

import java.util.List;
import java.util.Map;

public interface TaskRepository {

    /**
     * 初始化任务+任务分片
     */
    Task init(Task taskInstance, List<TaskSplit> taskSplit);

    /**
     * 获取需要分发的任务分片
     */
    List<TaskSplit> loadTaskSplitToDispatch();

    /**
     * 同步任务分片状态
     *
     * @param id
     * @param preStatus
     * @param targetStatus
     */
    void synTaskStatus(long id, TaskSplitStatusEnum preStatus, TaskSplitStatusEnum targetStatus);

    List<Long> loadNoFinishTasksIds(long idStart, int i);

    /**
     * @param taskIds
     * @return key：taskId，value#key：taskSplitStatus，value#value：count
     */
    Map<Long, Map<TaskSplitStatusEnum, Integer>> querySplitStatusMapByTaskIds(List<Long> taskIds);

    void taskFinalize(Task task, TaskStatusEnum taskStatus);

    Task queryTask(Long taskId);

    void updateSplitExecStatus(Long id, TaskSplitStatusEnum executing);
}
