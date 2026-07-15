package com.fraudwatch.fraudruleengine.ruleengine;

import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import com.fraudwatch.fraudruleengine.entity.Operator;
import org.springframework.stereotype.Component;

@Component
public class ContainsOperatorStrategy implements OperatorStrategy {

    @Override
    public boolean evaluate(Object actualValue, FraudRuleCondition condition) {
        if (actualValue == null || condition.getValue() == null) {
            return false;
        }
        String actual = String.valueOf(actualValue);
        return actual.contains(condition.getValue());
    }

    public Operator getSupportedOperator() {
        return Operator.CONTAINS;
    }
}
