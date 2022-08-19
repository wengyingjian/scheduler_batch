package com.wyj.test.impl;

import com.wyj.task.module.enums.TaskTypeEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MyTaskTypeEnum implements TaskTypeEnum {
    TYPE1(0), TYPE2(1);

    private final int type;

    @Override
    public Integer getType() {
        return type;
    }
}
