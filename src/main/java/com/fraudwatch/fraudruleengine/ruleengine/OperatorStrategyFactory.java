package com.fraudwatch.fraudruleengine.ruleengine;

import com.fraudwatch.fraudruleengine.entity.Operator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OperatorStrategyFactory {

    private final Map<Operator, OperatorStrategy> strategies;

    public OperatorStrategyFactory(List<OperatorStrategy> strategyList) {
        strategies = new HashMap<>();
        for (OperatorStrategy strategy : strategyList) {
            try {
                Operator op = (Operator) strategy.getClass().getMethod("getSupportedOperator").invoke(strategy);
                strategies.put(op, strategy);
            } catch (Exception e) {
                // Skip if strategy doesn't have getSupportedOperator
            }
        }
    }

    public OperatorStrategy getStrategy(Operator operator) {
        return strategies.get(operator);
    }
}
