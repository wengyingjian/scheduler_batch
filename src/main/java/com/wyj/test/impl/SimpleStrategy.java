package com.wyj.test.impl;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SimpleStrategy {
    private String strategyId;
    private List<Long> storeIds = new ArrayList<>();

    public static SimpleStrategy init(String strategyId) {
        SimpleStrategy strategy = new SimpleStrategy();
        strategy.setStrategyId(strategyId);
        return strategy;
    }
}
