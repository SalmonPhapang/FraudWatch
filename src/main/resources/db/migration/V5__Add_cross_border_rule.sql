-- Add the Cross-Border Transaction rule
-- This rule flags transactions where the merchant's country differs from the customer's location country

-- Insert the fraud rule
INSERT INTO fraud_rules (name, description, enabled, status, priority, severity, weight, action, rule_type, created_at, updated_at)
VALUES (
    'Cross-Border Transaction',
    'Flags transactions where the merchant''s country differs from the customer''s location country. Cross-border transactions carry higher fraud risk due to jurisdictional challenges, currency conversion, and increased anonymity.',
    true,
    'ACTIVE',
    70,
    50,
    40,
    'REVIEW',
    'MERCHANT',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert the condition for the rule (comparing merchant.country != location.country)
INSERT INTO fraud_rule_conditions (fraud_rule_id, field, operator, value, created_at)
SELECT id, 'merchant.country', 'FIELD_NOT_EQUALS', 'location.country', CURRENT_TIMESTAMP
FROM fraud_rules
WHERE name = 'Cross-Border Transaction';
