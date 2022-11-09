package com.wyj.task.core;

import com.wyj.task.TaskHandler;
import com.wyj.task.TaskStrategy;
import com.wyj.task.module.TaskSplit;
import com.wyj.task.module.enums.TaskExecResult;
import com.wyj.task.module.enums.TaskSplitStatusEnum;
import com.wyj.task.repository.TaskRepository;
import com.wyj.task.util.JsonUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.function.Consumer;

@DependsOn(value = {"taskStrategyContext"})
@Component
public class TaskMQConsumerRegister {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private TaskRepository taskRepository;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Value("${rocketmq.name-server}")
    private String nameservAddr;

    @Resource
    private TaskStrategyContext taskStrategyContext;

    /**
     * 任务层的处理
     */
    public TaskExecResult handleWithTaskWrapper(TaskSplit split) {
        logger.info("task mq consumer:taskId={},splitId={}", split.getTaskId(), split.getId());

        //get handler
        TaskHandler handler = TaskStrategyContext.getTask(split.getTaskType()).handler();

        //handle：不进行资源抢占，直接执行，业务系统需要保证幂等
        TaskExecResult result = TaskExecResult.RETRY;
        boolean error = false;
        try {
            result = handler.execute(split);
        } catch (Exception e) {
            logger.error("TaskHandler exec exception,splitId={}", split.getId(), e);
            error = true;
        } finally {
            logger.info("task mq result:taskId={},splitId={},err={},result={}",
                    split.getTaskId(), split.getId(), error, error ? null : result);
        }

        //process result
        //情况1：返回：执行成功，（任何状态）->（成功状态）
        //情况2：返回：执行终止，（任何状态）->（终止状态）
        //情况3：返回：重试，需要保证无限重试次数，这里需要调用客户端重发消息
        //情况4：返回：空，可能是客户端忘记实现，或者异常，处理方式同异常
        //情况5：返回：异常，需要保证有限重试次数。这里跟随rocketmq即可，通过中间件自动重试

        //空、异常，数据库更新执行次数，消息自动重试
        if (error || result == null) {
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
        }
        return result;
    }

    /**
     * 消息层的处理
     * <p>
     * 性能优化：若期望处理时间较短，重试的消息不会放回mq，而是在实例内部进行重试，最多重试1000次
     * 对于重试的任务，期望时间可能超过mq超时时间（或重试超过1000次），将放弃优化，放回到mq
     */
    public void handleWithMessageWrapper(TaskSplit split) {
        //mq默认超时时间，单次执行需要控制在该时间内
        long deadLine = System.currentTimeMillis() + (15 - 1) * 60 * 1000L;
        int retry = 0;
        long maxCost = 1;

        TaskExecResult result;
        long predict;
        do {
            retry++;
            //before-统计信息
            long start = System.currentTimeMillis();

            //处理消息
            result = handleWithTaskWrapper(split);
            logger.info("retry inside");

            //after-统计信息
            maxCost = Math.max(maxCost, System.currentTimeMillis() - start);
            predict = System.currentTimeMillis() + maxCost;
        } while (result == TaskExecResult.RETRY && predict < deadLine && retry < 1000);

        //对于重试的任务，期望时间可能超过mq超时时间（或重试超过1000次），将放弃优化，放回到mq
        if (result == TaskExecResult.RETRY) {
            String mqTopic = TaskStrategyContext.getTaskTopic(split.getTaskType());
            rocketMQTemplate.convertAndSend(mqTopic, JsonUtil.obj2String(split));
            logger.info("retry outside");
        }
    }

    /**
     * 因为不同的任务使用不同的topic、consumer group，所以这里根据task注册不同的consumer listener
     * topic = task_topic_ + ${task_name}
     * consumerGroup = task_consumer_group_ + ${task_name}
     */
    @PostConstruct
    public void init() throws Exception {
        //1.获取所有的任务
        List<TaskStrategy> tasks = TaskStrategyContext.getTasks();

        //2.注册各个任务的consumer
        for (TaskStrategy task : tasks) {
            String mqTopic = TaskStrategyContext.getTaskTopic(task.getTaskType().getType());
            String consumerGroup = TaskStrategyContext.getTaskConsumerGroup(task.getTaskType().getType());
            initConsumer(mqTopic, consumerGroup, this::handleWithMessageWrapper);
        }
    }

    private void initConsumer(String mqTopic, String consumerGroup, Consumer<TaskSplit> c) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(nameservAddr);
        consumer.setConsumerGroup(consumerGroup);
        consumer.subscribe(mqTopic, "*");
        //timeout
//        consumer.setConsumeTimeout(15);
        consumer.setConsumeThreadMax(20);

        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            //获取消息
            for (MessageExt ext : msgs) {
                TaskSplit split = JsonUtil.string2Obj(new String(ext.getBody()), TaskSplit.class);
                c.accept(split);
            }
            //处理结果
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        //启动消费者
        consumer.start();
    }


}
