package com.wyj.task;

import com.wyj.test.BatchTaskApplication;
import com.wyj.task.core.TaskService;
import com.wyj.test.impl.TaskTypeEnum;
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
        taskClient.submit(TaskTypeEnum.SIMPLE_FLUSH_CACHE, new Date(), String.valueOf(new Random().nextInt(100000)));
    }

    @Test
    public void testCreateMultiTask() {
        taskClient.submit(TaskTypeEnum.MULTI_TASK_TEST, new Date(), null);
    }

    @Test
    public void testDispatch() {
        taskService.dispatch();
    }

    @Test
    public void testConsume() {
    }

    @Test
    public void testFinalize() {
        taskService.scan();
    }

}
