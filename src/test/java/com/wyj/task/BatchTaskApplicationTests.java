package com.wyj.task;

import com.wyj.test.BatchTaskApplication;
import com.wyj.task.core.TaskService;
import com.wyj.test.impl.MyTaskTypeEnum;
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
    public void testCreateTask() {
        taskClient.submit(MyTaskTypeEnum.TYPE1, new Date(), String.valueOf(new Random().nextInt(100000)));
    }

    @Test
    public void testDispatch() {
        taskService.map();
    }

    @Test
    public void testConsume() {
    }

    @Test
    public void testFinalize() {
        taskService.reduce();
    }

}
