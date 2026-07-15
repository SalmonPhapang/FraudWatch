
-- Add rule status enum and column
CREATE TYPE rule_status AS ENUM ('ACTIVE', 'DRAFT', 'SHADOW');

-- Add status column to fraud_rules table
ALTER TABLE fraud_rules ADD COLUMN status rule_status NOT NULL DEFAULT 'ACTIVE';

-- Update existing indexes to include status
CREATE INDEX idx_fraud_rules_enabled_status ON fraud_rules(enabled, status, priority DESC);
