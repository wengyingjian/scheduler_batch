package com.wyj.test.impl;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SimpleCache {
    private String dataId;
    private List<Long> storeIds = new ArrayList<>();

    public static SimpleCache init(String strategyId) {
        SimpleCache strategy = new SimpleCache();
        strategy.setDataId(strategyId);
        return strategy;
    }
}
