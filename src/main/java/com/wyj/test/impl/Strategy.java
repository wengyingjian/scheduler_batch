package com.wyj.test.impl.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Strategy {
    private String strategyId;
    private List<Long> storeIds = new ArrayList<>();

    public static Strategy init(String strategyId) {
        Strategy strategy = new Strategy();
        strategy.setStrategyId(strategyId);
        return strategy;
    }
}
