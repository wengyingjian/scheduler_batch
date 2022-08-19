package com.wyj.task.repository.mapper;

import com.wyj.task.repository.entity.TaskPO;
import com.wyj.task.repository.entity.TaskSplitPO;

import java.util.List;
import java.util.Map;

public interface TaskMapper {
    int insert(TaskPO taskPO);

    int batchInsertSplit(List<TaskSplitPO> splitPO);

    /**
     * 查询待分发的任务
     */
    List<TaskSplitPO> queryTaskSplitToDispatch(long idStart, int limit);

    /**
     * 更新任务分片状态：初始化->执行中
     */
    int updateTaskSplitStatus(long splitId, int preStatus, int targetStatus);

    /**
     * 更新任务执行状态，自增执行次数
     *
     * @param targetStatus 若为null，代表不修改状态
     */
    void updateTaskSplitExecStatus(Long splitId, Integer targetStatus);

    /**
     * 查询未终态的任务
     */
    List<Long> queryNotFinishTaskIds(long idStart, int limit);

    /**
     * 根据任务id统计分片的状态
     */
    List<Map> querySplitStatusMapByTaskIds(List<Long> taskIds);

    /**
     * 根据id查询任务
     */
    TaskPO queryTask(Long taskId);

    /**
     * 更新任务终态
     */
    void updateTaskStatus(Long taskId, int taskStatus);
}
