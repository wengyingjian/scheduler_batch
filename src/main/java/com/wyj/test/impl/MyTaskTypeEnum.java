package com.wyj.test.impl;

import com.wyj.task.module.enums.TaskTypeEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MyTaskTypeEnum implements TaskTypeEnum {
    SIMPLE_FLUSH_CACHE(0), MULTI_TASK_TEST(1);

    private final int type;

    @Override
    public Integer getType() {
        return type;
    }
}
