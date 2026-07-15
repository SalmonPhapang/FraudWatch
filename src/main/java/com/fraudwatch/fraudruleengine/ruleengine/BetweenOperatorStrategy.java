package com.fraudwatch.fraudruleengine.ruleengine;

import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import com.fraudwatch.fraudruleengine.entity.Operator;
import org.springframework.stereotype.Component;

@Component
public class BetweenOperatorStrategy implements OperatorStrategy {

    @Override
    public boolean evaluate(Object actualValue, FraudRuleCondition condition) {
        int compareMin = ComparisonHelper.compare(actualValue, condition.getValueMin());
        int compareMax = ComparisonHelper.compare(actualValue, condition.getValueMax());
        return compareMin >= 0 && compareMax <= 0;
    }

    public Operator getSupportedOperator() {
        return Operator.BETWEEN;
    }
}
