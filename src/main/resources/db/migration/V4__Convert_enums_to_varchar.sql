-- Convert PostgreSQL enum columns to VARCHAR
-- Hibernate with @Enumerated(EnumType.STRING) binds Java enum names as
-- character varying parameters, which PostgreSQL rejects when the column
-- is a custom enum type (fraud_action, rule_type, operator, etc.)
--
-- Using VARCHAR columns is simpler and more portable with Hibernate/JPA.
-- The Java-side enum validation is sufficient for data integrity.

-- fraud_rules.action (type: fraud_action)
ALTER TABLE fraud_rules ALTER COLUMN action TYPE VARCHAR(50);

-- fraud_rules.rule_type (type: rule_type)
ALTER TABLE fraud_rules ALTER COLUMN rule_type TYPE VARCHAR(50);

-- fraud_rules.status (type: rule_status, added by V3)
ALTER TABLE fraud_rules ALTER COLUMN status TYPE VARCHAR(50);

-- fraud_events.decision (type: fraud_action)
ALTER TABLE fraud_events ALTER COLUMN decision TYPE VARCHAR(50);

-- fraud_rule_conditions.operator (type: operator)
ALTER TABLE fraud_rule_conditions ALTER COLUMN operator TYPE VARCHAR(50);

-- fraud_rule_groups.logical_operator (type: logical_operator)
ALTER TABLE fraud_rule_groups ALTER COLUMN logical_operator TYPE VARCHAR(50);
