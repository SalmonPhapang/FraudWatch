package com.fraudwatch.fraudruleengine.ruleengine;

import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import com.fraudwatch.fraudruleengine.entity.Operator;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EqualsOperatorStrategy implements OperatorStrategy {

    @Override
    public boolean evaluate(Object actualValue, FraudRuleCondition condition) {
        if (actualValue == null || condition.getValue() == null) {
            return Objects.equals(actualValue, condition.getValue());
        }
        String expected = condition.getValue();
        String actual = String.valueOf(actualValue);
        return expected.equals(actual);
    }

    public Operator getSupportedOperator() {
        return Operator.EQUALS;
    }
}
