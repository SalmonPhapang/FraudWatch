package com.fraudwatch.fraudruleengine.ruleengine;

import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import com.fraudwatch.fraudruleengine.entity.Operator;
import org.springframework.stereotype.Component;

@Component
public class GreaterThanOperatorStrategy implements OperatorStrategy {

    @Override
    public boolean evaluate(Object actualValue, FraudRuleCondition condition) {
        return ComparisonHelper.compare(actualValue, condition.getValue()) > 0;
    }

    public Operator getSupportedOperator() {
        return Operator.GREATER_THAN;
    }
}
