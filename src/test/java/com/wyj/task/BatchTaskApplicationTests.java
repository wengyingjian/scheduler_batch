package com.wyj.task;

import com.wyj.task.core.TaskScheduler;
import com.wyj.test.BatchTaskApplication;
import com.wyj.task.core.TaskService;
import com.wyj.test.impl.MyTaskTypeEnum;
import org.apache.rocketmq.client.apis.ClientException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;

@SpringBootTest(classes = BatchTaskApplication.class)
class BatchTaskApplicationTests {

    @Resource
    private TaskService taskService;
    @Resource
    private TaskApi taskClient;

    @Test
    public void testCreateSimpleTask() {
        taskClient.submit(MyTaskTypeEnum.SIMPLE_FLUSH_CACHE, new Date(), String.valueOf(new Random().nextInt(100000)));
    }

    @Test
    public void testCreateMultiTask() {
        taskClient.submit(MyTaskTypeEnum.MULTI_TASK_TEST, new Date(), null);
    }

    @Test
    public void testDispatch() throws ClientException {
        taskService.dispatch(new TaskScheduler.StopChecker(-1));
    }

    @Test
    public void testConsume() {
    }

    @Test
    public void testScan() {
        taskService.scan(new TaskScheduler.StopChecker(-1));
    }

}
