package com.fraudwatch.fraudruleengine.ruleengine;

import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import com.fraudwatch.fraudruleengine.entity.Operator;
import org.springframework.stereotype.Component;

@Component
public class NotEqualsOperatorStrategy implements OperatorStrategy {

    @Override
    public boolean evaluate(Object actualValue, FraudRuleCondition condition) {
        EqualsOperatorStrategy equals = new EqualsOperatorStrategy();
        return !equals.evaluate(actualValue, condition);
    }

    public Operator getSupportedOperator() {
        return Operator.NOT_EQUALS;
    }
}
