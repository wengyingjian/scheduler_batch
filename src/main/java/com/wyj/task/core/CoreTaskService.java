package com.wyj.task.core;

import com.wyj.task.TaskApi;
import com.wyj.task.module.enums.TaskSplitStatusEnum;
import com.wyj.task.module.enums.TaskStatusEnum;
import com.wyj.task.module.enums.TaskTypeEnum;
import com.wyj.task.module.Task;
import com.wyj.task.module.TaskSplit;
import com.wyj.task.repository.TaskRepository;
import com.wyj.task.util.JsonUtil;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CoreTaskService implements TaskService, TaskApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private TaskRepository taskRepository;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void submit(TaskTypeEnum taskType, Date taskTime, String bizData) {
        //1.分片
        Task task = Task.init(taskType.getType(), taskTime, bizData);
        List<TaskSplit> taskSplit = TaskStrategyContext.getTask(taskType).shardingStrategy().sharding(task);

        //2.数据库task、taskSplit
        taskRepository.init(task, taskSplit);
    }

    @Override
    public void dispatch(TaskScheduler.StopChecker checker) throws ClientException {
        List<TaskSplit> taskSplitList;
        long idStart = 0;
        int limit = 100;
        while (!checker.check()) {
            //查询待处理任务：任务状态，任务处理时间
            taskSplitList = taskRepository.queryTaskSplitToDispatch(idStart, limit);
            if (CollectionUtils.isEmpty(taskSplitList)) {
                break;
            }
            idStart = taskSplitList.get(taskSplitList.size() - 1).getId();

            //分发+修改状态
            for (TaskSplit split : taskSplitList) {
                doDispatch(split);
            }
        }
    }

    /**
     * 因为是任务，可以重试，所以不需要事务消息
     * 先发消息，再更新数据库；如果数据库操作失败，重试即可
     *
     * @param split
     */
    private void doDispatch(TaskSplit split) throws ClientException {
        //分发任务
        String mqTopic = TaskStrategyContext.getTaskTopic(split.getTaskType());
        rocketMQTemplate.convertAndSend(mqTopic, JsonUtil.obj2String(split));

        //修改任务状态
        taskRepository.statusSplit2Executing(split.getId(), TaskSplitStatusEnum.INIT, TaskSplitStatusEnum.EXECUTING);

        logger.info("task dispatch succ,splitId={},split={}", split.getId(), JsonUtil.obj2String(split));
    }

    @Override
    public void scan(TaskScheduler.StopChecker checker) {
        long idStart = 0L;
        int size = 100;
        //查询所有未完结任务
        List<Long> taskIds = taskRepository.loadNoFinishTasksIds(idStart, size);
        //统计未完结任务的分片状态
        Map<Long, Map<TaskSplitStatusEnum, Integer>> taskSplitStatusMap =
                taskRepository.querySplitStatusMapByTaskIds(taskIds);

        for (Long taskId : taskIds) {
            Map<TaskSplitStatusEnum, Integer> taskSplitStatus = taskSplitStatusMap.get(taskId);
            //该未完结任务没有分片数据
            if (taskSplitStatus == null) {
                continue;
            }
            //该未完结任务仍存在未完结的分片：不处理
            if (taskSplitStatus.get(TaskSplitStatusEnum.INIT) != null
                    || taskSplitStatus.get(TaskSplitStatusEnum.EXECUTING) != null) {
                continue;
            }
            //剩下的情况就是：所有子任务已完结
            Task task = taskRepository.queryTask(taskId);

            //1.需要调用finalize，同样，业务系统需要做好幂等处理
            TaskStrategyContext.getTask(task.getTaskType()).taskHandler().reduce(task);

            //2.需要更新任务状态：若全部成功，更新为SUCCESS；若部分停止，更新为FINISH
            TaskStatusEnum taskStatus =
                    (taskSplitStatus.get(TaskSplitStatusEnum.STOP) == null || taskSplitStatus.get(TaskSplitStatusEnum.STOP) == 0)
                            ? TaskStatusEnum.SUCCESS : TaskStatusEnum.FINISH;
            taskRepository.updateTaskFinalizeStatus(task.getId(), taskStatus);
        }
    }

}
