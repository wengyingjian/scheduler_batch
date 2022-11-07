package com.wyj.task.core;

import com.wyj.task.TaskHandler;
import com.wyj.task.module.enums.TaskExecResult;
import com.wyj.task.module.enums.TaskSplitStatusEnum;
import com.wyj.task.module.TaskSplit;
import com.wyj.task.repository.TaskRepository;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@RocketMQMessageListener(topic = "${rocketmq.consumer.topic}", consumerGroup = "${rocketmq.consumer.group}")
public class TaskMQConsumer implements RocketMQListener<TaskSplit> {

    @Resource
    private TaskRepository taskRepository;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Value("${rocketmq.producer.topic}")
    private String mqTopic;

    @Override
    public void onMessage(TaskSplit split) {
        //get handler
        TaskHandler handler = TaskStrategyContext.getTask(split.getTaskType()).handler();

        //handle：不进行资源抢占，直接执行，业务系统需要保证幂等
        TaskExecResult result = TaskExecResult.RETRY;
        boolean error = false;
        try {
            result = handler.execute(split);
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
        }

        //process result
        //情况1：返回：执行成功，（任何状态）->（成功状态）
        //情况2：返回：执行终止，（任何状态）->（终止状态）
        //情况3：返回：重试，需要保证无限重试次数，这里需要调用客户端重发消息
        //情况4：返回：空，可能是客户端忘记实现，或者异常，处理方式同异常
        //情况5：返回：异常，需要保证有限重试次数。这里跟随rocketmq即可，通过中间件自动重试

        //空、异常，数据库更新执行次数，消息自动重试
        if (result == null || error) {
            taskRepository.updateSplitExecStatus(split.getId(), null);
            throw new RuntimeException("task exec failed,throw to retry mq");
        }

        if (result == TaskExecResult.SUCCESS) {
            //执行成功，数据库：（任何状态）->（成功状态），消息：处理成功
            taskRepository.updateSplitExecStatus(split.getId(), TaskSplitStatusEnum.SUCCESS);

        } else if (result == TaskExecResult.STOP) {
            //执行终止，数据库（任何状态）->（终止状态），消息：处理成功
            taskRepository.updateSplitExecStatus(split.getId(), TaskSplitStatusEnum.STOP);

        } else if (result == TaskExecResult.RETRY) {
            //执行终止，数据库更新执行次数，消息：返回成功，重新发送
            taskRepository.updateSplitExecStatus(split.getId(), null);
            rocketMQTemplate.send(mqTopic, MessageBuilder.withPayload(split).build());
        }
    }

}
