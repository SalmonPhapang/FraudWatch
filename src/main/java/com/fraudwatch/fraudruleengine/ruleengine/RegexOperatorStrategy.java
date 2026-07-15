package com.fraudwatch.fraudruleengine.ruleengine;

import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import com.fraudwatch.fraudruleengine.entity.Operator;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RegexOperatorStrategy implements OperatorStrategy {

    @Override
    public boolean evaluate(Object actualValue, FraudRuleCondition condition) {
        if (actualValue == null || condition.getValue() == null) {
            return false;
        }
        String actual = String.valueOf(actualValue);
        try {
            Pattern pattern = Pattern.compile(condition.getValue());
            return pattern.matcher(actual).matches();
        } catch (Exception e) {
            return false;
        }
    }

    public Operator getSupportedOperator() {
        return Operator.REGEX;
    }
}
