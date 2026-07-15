
package com.fraudwatch.fraudruleengine.ruleengine;

import com.fraudwatch.fraudruleengine.entity.FraudRuleCondition;
import com.fraudwatch.fraudruleengine.entity.Operator;
import com.fraudwatch.fraudruleengine.service.VelocityService;
import org.springframework.stereotype.Component;

@Component
public class VelocityOperatorStrategy implements OperatorStrategy {

    private final VelocityService velocityService;

    public VelocityOperatorStrategy(VelocityService velocityService) {
        this.velocityService = velocityService;
    }

    @Override
    public boolean evaluate(Object actualValue, FraudRuleCondition condition) {
        // For velocity conditions:
        // - field: the key (e.g., "customer.id:transactions")
        // - value: "threshold:windowSeconds" (e.g., "10:600" for 10 events in 10 minutes)
        if (condition.getValue() == null) {
            return false;
        }
        String[] parts = condition.getValue().split(":");
        if (parts.length != 2) {
            return false;
        }
        try {
            long threshold = Long.parseLong(parts[0]);
            long windowSecs = Long.parseLong(parts[1]);
            // Build the velocity key from actualValue (which is the field's value)
            String velocityKey = condition.getField() + ":" + (actualValue != null ? actualValue : "null");
            long count = velocityService.incrementAndGet(velocityKey, windowSecs);

            return switch (condition.getOperator()) {
                case VELOCITY_GT -> count > threshold;
                case VELOCITY_LT -> count < threshold;
                case VELOCITY_GTE -> count >= threshold;
                case VELOCITY_LTE -> count <= threshold;
                case VELOCITY_EQ -> count == threshold;
                default -> false;
            };
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Wait, OperatorStrategyFactory expects getSupportedOperator() but this handles multiple operators!
    // So let's create separate small wrapper strategies for each velocity operator!
}
