-- Seed the 3 core fraud rules that the scenarios reference
-- These were never created in V1 (only table structure), so they need to exist for the fraud check to work.

-- ============================================================
-- Rule 1: New Account High Amount
-- Action: REVIEW (flag for manual review)
-- Condition: accountAgeDays < 7 AND amount > 1000
-- ============================================================
INSERT INTO fraud_rules (name, description, enabled, status, priority, severity, weight, action, rule_type, created_at, updated_at)
VALUES (
    'New Account High Amount',
    'Flag transactions from new accounts (less than 7 days old) with payment amounts exceeding R1,000. Common pattern for account takeover and first-party fraud.',
    true,
    'ACTIVE',
    10,
    40,
    50,
    'REVIEW',
    'PAYMENT',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO fraud_rule_conditions (fraud_rule_id, field, operator, value, created_at)
SELECT id, 'customer.accountAgeDays', 'LESS_THAN', '7', CURRENT_TIMESTAMP
FROM fraud_rules WHERE name = 'New Account High Amount';

INSERT INTO fraud_rule_conditions (fraud_rule_id, field, operator, value, created_at)
SELECT id, 'payment.amount', 'GREATER_THAN', '1000', CURRENT_TIMESTAMP
FROM fraud_rules WHERE name = 'New Account High Amount';

-- ============================================================
-- Rule 2: High Transaction Velocity
-- Action: BLOCK (automatic block)
-- Condition: more than 5 transactions from the same customer in 10 minutes
-- ============================================================
INSERT INTO fraud_rules (name, description, enabled, status, priority, severity, weight, action, rule_type, created_at, updated_at)
VALUES (
    'High Transaction Velocity',
    'Block customers exceeding 5 transactions in a 10-minute sliding window. Indicates automated or bot-like behavior.',
    true,
    'ACTIVE',
    20,
    80,
    100,
    'BLOCK',
    'CUSTOMER',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO fraud_rule_conditions (fraud_rule_id, field, operator, value, created_at)
SELECT id, 'customer.id', 'VELOCITY_GT', '5:600', CURRENT_TIMESTAMP
FROM fraud_rules WHERE name = 'High Transaction Velocity';

-- ============================================================
-- Rule 3: High Amount Transaction
-- Action: BLOCK (automatic block)
-- Condition: amount > 30000
-- ============================================================
INSERT INTO fraud_rules (name, description, enabled, status, priority, severity, weight, action, rule_type, created_at, updated_at)
VALUES (
    'High Amount Transaction',
    'Block single transactions exceeding R30,000. High-value transactions carry elevated fraud risk and warrant automatic blocking.',
    true,
    'ACTIVE',
    30,
    90,
    80,
    'BLOCK',
    'PAYMENT',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO fraud_rule_conditions (fraud_rule_id, field, operator, value, created_at)
SELECT id, 'payment.amount', 'GREATER_THAN', '30000', CURRENT_TIMESTAMP
FROM fraud_rules WHERE name = 'High Amount Transaction';
