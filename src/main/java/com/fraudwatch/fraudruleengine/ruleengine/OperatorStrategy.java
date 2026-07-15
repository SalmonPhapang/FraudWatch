package com.fraudwatch.fraudruleengine.ruleengine;

import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;

public interface OperatorStrategy {
    boolean evaluate(Object actualValue, FraudRuleCondition condition);
}
