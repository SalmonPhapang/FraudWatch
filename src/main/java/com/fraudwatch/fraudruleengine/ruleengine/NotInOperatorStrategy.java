package com.fraudwatch.fraudruleengine.ruleengine;

import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import com.fraudwatch.fraudruleengine.entity.Operator;
import org.springframework.stereotype.Component;

@Component
public class NotInOperatorStrategy implements OperatorStrategy {

    @Override
    public boolean evaluate(Object actualValue, FraudRuleCondition condition) {
        InOperatorStrategy in = new InOperatorStrategy();
        return !in.evaluate(actualValue, condition);
    }

    public Operator getSupportedOperator() {
        return Operator.NOT_IN;
    }
}
