package com.wyj.task.repository;

import com.wyj.task.module.enums.TaskSplitStatusEnum;
import com.wyj.task.module.enums.TaskStatusEnum;
import com.wyj.task.module.Task;
import com.wyj.task.module.TaskSplit;

import java.util.List;
import java.util.Map;

public interface TaskRepository {

    /**
     * 初始化任务+任务分片
     */
    Task init(Task task, List taskSplit);

    /**
     * 获取需要分发的任务分片
     */
    List<TaskSplit> queryTaskSplitToDispatch(long idStart, int limit);

    /**
     * 同步任务分片状态
     */
    boolean statusSplit2Executing(long splitId, TaskSplitStatusEnum preStatus, TaskSplitStatusEnum targetStatus);

    /**
     * 更新任务执行状态，自增执行次数
     *
     * @param targetStatus 若为null，代表不修改状态
     */
    void updateSplitExecStatus(Long splitId, TaskSplitStatusEnum targetStatus);

    /**
     * 同步任务分片状态
     */
    void synTaskStatus(long id, TaskSplitStatusEnum preStatus, TaskSplitStatusEnum targetStatus);

    /**
     * 查询已开始、未完结的任务
     */
    List<Long> loadNoFinishTasksIds(long idStart, int limit);

    /**
     * 根据任务id统计分片的状态
     *
     * @return key：taskId，value#key：taskSplitStatus，value#value：count
     */
    Map<Long, Map<TaskSplitStatusEnum, Integer>> querySplitStatusMapByTaskIds(List<Long> taskIds);

    Task queryTask(Long taskId);

    /**
     * 更新任务终态
     */
    void updateTaskFinalizeStatus(Long taskId, TaskStatusEnum taskStatus);
}
