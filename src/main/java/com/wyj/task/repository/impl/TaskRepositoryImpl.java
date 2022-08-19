package com.wyj.task.repository.impl;

import com.wyj.task.module.enums.TaskSplitStatusEnum;
import com.wyj.task.module.enums.TaskStatusEnum;
import com.wyj.task.module.Task;
import com.wyj.task.module.TaskSplit;
import com.wyj.task.repository.TaskRepository;
import com.wyj.task.repository.entity.TaskPO;
import com.wyj.task.repository.entity.TaskSplitPO;
import com.wyj.task.repository.mapper.TaskMapper;
import com.wyj.task.repository.transfer.TaskTransfer;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class TaskRepositoryImpl implements TaskRepository {

    @Resource
    private TaskMapper taskMapper;

    private String mqTopicName;

    @Override
    public Task init(Task task, List taskSplit) {
        TaskPO taskPO = TaskTransfer.tranfer(task);
        taskPO.setStatus(TaskStatusEnum.INIT.getStatus());
        taskPO.setCreateTime(new Date());
        taskPO.setUpdateTime(new Date());
        taskMapper.insert(taskPO);

        List<TaskSplitPO> splitPO = TaskTransfer.tranfer(taskPO, taskSplit);
        taskMapper.batchInsertSplit(splitPO);

        return TaskTransfer.tranfer(taskPO);
    }

    @Override
    public List<TaskSplit> queryTaskSplitToDispatch(long idStart, int limit) {
        List<TaskSplitPO> splitPO = taskMapper.queryTaskSplitToDispatch(idStart, limit);
        return TaskTransfer.tranferSplitList(splitPO);
    }

    @Override
    public boolean statusSplit2Executing(long splitId, TaskSplitStatusEnum preStatus, TaskSplitStatusEnum targetStatus) {
        return taskMapper.updateTaskSplitStatus(splitId, preStatus.getStatus(), targetStatus.getStatus()) != 0;
    }

    @Override
    public void updateSplitExecStatus(Long splitId, TaskSplitStatusEnum targetStatus) {
        taskMapper.updateTaskSplitExecStatus(splitId, targetStatus == null ? null : targetStatus.getStatus());
    }

    @Override
    public void synTaskStatus(long id, TaskSplitStatusEnum preStatus, TaskSplitStatusEnum targetStatus) {

    }

    @Override
    public List<Long> loadNoFinishTasksIds(long idStart, int limit) {
        return taskMapper.queryNotFinishTaskIds(idStart, limit);
    }

    @Override
    public Map<Long, Map<TaskSplitStatusEnum, Integer>> querySplitStatusMapByTaskIds(List<Long> taskIds) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return Collections.emptyMap();
        }

        List<Map> groupList = taskMapper.querySplitStatusMapByTaskIds(taskIds);

        Map<Long, Map<TaskSplitStatusEnum, Integer>> map = new HashMap<>();
        for (Map result : groupList) {
            long count = (long) (result.get("value"));
            String[] keys = ((String) result.get("key")).split("_");
            long taskId = Long.parseLong(keys[0]);
            Integer status = Integer.parseInt(keys[1]);

            Map<TaskSplitStatusEnum, Integer> taskIdMap = map.computeIfAbsent(taskId, k -> new HashMap<>());
            taskIdMap.put(TaskSplitStatusEnum.valueOf(status), (int) count);
        }
        return map;
    }

    @Override
    public Task queryTask(Long taskId) {
        TaskPO  taskPO = taskMapper.queryTask(taskId);
        return TaskTransfer.tranfer(taskPO);
    }

    @Override
    public void updateTaskFinalizeStatus(Long taskId, TaskStatusEnum taskStatus) {
        taskMapper.updateTaskStatus(taskId,taskStatus.getStatus());
    }



}
