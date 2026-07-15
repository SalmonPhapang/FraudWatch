package com.fraudwatch.fraudruleengine.ruleengine;

import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import com.fraudwatch.fraudruleengine.entity.Operator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class InOperatorStrategy implements OperatorStrategy {

    @Override
    public boolean evaluate(Object actualValue, FraudRuleCondition condition) {
        if (actualValue == null || condition.getValue() == null) {
            return false;
        }
        List<String> values = Arrays.asList(condition.getValue().split(","));
        String actual = String.valueOf(actualValue);
        return values.contains(actual);
    }

    public Operator getSupportedOperator() {
        return Operator.IN;
    }
}
