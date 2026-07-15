package com.fraudwatch.fraudruleengine.ruleengine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudwatch.fraudruleengine.dto.FraudCheckRequest;
import com.fraudwatch.fraudruleengine.entity.FraudRule;
import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RuleEvaluator {

    private final OperatorStrategyFactory operatorStrategyFactory;
    private final ObjectMapper objectMapper;

    public RuleEvaluator(OperatorStrategyFactory operatorStrategyFactory, ObjectMapper objectMapper) {
        this.operatorStrategyFactory = operatorStrategyFactory;
        this.objectMapper = objectMapper;
    }

    public boolean evaluate(FraudRule rule, Map<String, Object> requestMap) {
        List<FraudRuleCondition> conditions = rule.getConditions();
        if (conditions == null || conditions.isEmpty()) {
            return false;
        }
        for (FraudRuleCondition condition : conditions) {
            Object actualValue = getFieldValue(requestMap, condition.getField());
            OperatorStrategy strategy = operatorStrategyFactory.getStrategy(condition.getOperator());
            if (strategy == null || !strategy.evaluate(actualValue, condition)) {
                return false;
            }
        }
        return true;
    }

    public boolean evaluate(FraudRule rule, FraudCheckRequest request) {
        Map<String, Object> requestMap = objectMapper.convertValue(request, Map.class);
        return evaluate(rule, requestMap);
    }

    private Object getFieldValue(Map<String, Object> map, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        Object current = map;
        for (String part : parts) {
            if (current == null || !(current instanceof Map)) {
                return null;
            }
            current = ((Map<String, Object>) current).get(part);
        }
        return current;
    }

    public List<FraudRule> evaluateAll(List<FraudRule> rules, FraudCheckRequest request) {
        Map<String, Object> requestMap = objectMapper.convertValue(request, Map.class);
        List<FraudRule> matched = new ArrayList<>();
        for (FraudRule rule : rules) {
            if (evaluate(rule, requestMap)) {
                matched.add(rule);
            }
        }
        return matched;
    }
}
