
package com.fraudwatch.fraudruleengine.ruleengine;

import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import com.fraudwatch.fraudruleengine.entity.Operator;
import org.springframework.stereotype.Component;

@Component
public class VelocityLessThanOperatorStrategy implements OperatorStrategy {

    private final VelocityOperatorStrategy velocityOperatorStrategy;

    public VelocityLessThanOperatorStrategy(VelocityOperatorStrategy velocityOperatorStrategy) {
        this.velocityOperatorStrategy = velocityOperatorStrategy;
    }

    @Override
    public boolean evaluate(Object actualValue, FraudRuleCondition condition) {
        return velocityOperatorStrategy.evaluate(actualValue, condition);
    }

    public Operator getSupportedOperator() {
        return Operator.VELOCITY_LT;
    }
}
