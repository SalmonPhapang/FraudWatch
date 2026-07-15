-- Initial schema for Fraud Rule Engine

-- Enums
CREATE TYPE fraud_action AS ENUM ('ALLOW', 'REVIEW', 'BLOCK');
CREATE TYPE rule_type AS ENUM ('CUSTOMER', 'DEVICE', 'PAYMENT', 'LOCATION', 'MERCHANT', 'AUTHENTICATION');
CREATE TYPE operator AS ENUM ('=', '!=', '<', '>', '<=', '>=', 'BETWEEN', 'IN', 'NOT_IN', 'CONTAINS', 'STARTS_WITH', 'ENDS_WITH', 'REGEX');
CREATE TYPE logical_operator AS ENUM ('AND', 'OR');
CREATE TYPE blacklist_type AS ENUM ('DEVICE', 'CARD', 'ADDRESS', 'USER', 'MERCHANT');

-- Users table for authentication
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

-- Fraud rules table
CREATE TABLE fraud_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    enabled BOOLEAN NOT NULL DEFAULT true,
    priority INTEGER NOT NULL DEFAULT 0,
    severity INTEGER NOT NULL DEFAULT 0,
    weight INTEGER NOT NULL DEFAULT 0,
    action fraud_action NOT NULL,
    rule_type rule_type NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

-- Fraud rule groups (for nested conditions)
CREATE TABLE fraud_rule_groups (
    id BIGSERIAL PRIMARY KEY,
    fraud_rule_id BIGINT NOT NULL REFERENCES fraud_rules(id),
    parent_group_id BIGINT REFERENCES fraud_rule_groups(id),
    logical_operator logical_operator NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Fraud rule conditions
CREATE TABLE fraud_rule_conditions (
    id BIGSERIAL PRIMARY KEY,
    fraud_rule_id BIGINT NOT NULL REFERENCES fraud_rules(id),
    group_id BIGINT REFERENCES fraud_rule_groups(id),
    field VARCHAR(200) NOT NULL,
    operator operator NOT NULL,
    value TEXT,
    value_min TEXT,
    value_max TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Fraud events
CREATE TABLE fraud_events (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(100),
    customer_id VARCHAR(100),
    device_id VARCHAR(100),
    merchant_id VARCHAR(100),
    risk_score INTEGER NOT NULL,
    decision fraud_action NOT NULL,
    request_data JSONB,
    matched_rules TEXT[],
    reason_codes TEXT[],
    evaluation_time_ms BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Blacklisted devices
CREATE TABLE fraud_blacklisted_devices (
    id BIGSERIAL PRIMARY KEY,
    device_id VARCHAR(100) NOT NULL UNIQUE,
    fingerprint VARCHAR(255),
    reason TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

-- Blacklisted cards
CREATE TABLE fraud_blacklisted_cards (
    id BIGSERIAL PRIMARY KEY,
    card_hash VARCHAR(255) NOT NULL UNIQUE,
    last_four VARCHAR(4),
    reason TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

-- Blacklisted addresses
CREATE TABLE fraud_blacklisted_addresses (
    id BIGSERIAL PRIMARY KEY,
    address_hash VARCHAR(255) NOT NULL UNIQUE,
    address TEXT,
    reason TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

-- Blacklisted users
CREATE TABLE fraud_blacklisted_users (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL UNIQUE,
    reason TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

-- Blacklisted merchants
CREATE TABLE fraud_blacklisted_merchants (
    id BIGSERIAL PRIMARY KEY,
    merchant_id VARCHAR(100) NOT NULL UNIQUE,
    reason TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

-- Fraud scores history
CREATE TABLE fraud_scores (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(100),
    session_id VARCHAR(100),
    score INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Risk thresholds configuration
CREATE TABLE risk_thresholds (
    id BIGSERIAL PRIMARY KEY,
    allow_max INTEGER NOT NULL DEFAULT 30,
    review_max INTEGER NOT NULL DEFAULT 70,
    block_max INTEGER NOT NULL DEFAULT 100,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert initial risk thresholds
INSERT INTO risk_thresholds (allow_max, review_max, block_max) VALUES (30, 70, 100);

-- Insert default admin user (password: admin123, BCrypt encoded)
INSERT INTO users (username, password, email, role, enabled) VALUES
('admin', '$2a$10$C6NV0IciicQcgMFc95e5FONIs6/klXdEEFKU/pdk6WN7DssKjD3nW', 'admin@fraudwatch.com', 'ADMIN', true);

-- Create indexes
CREATE INDEX idx_fraud_rules_enabled ON fraud_rules(enabled, priority DESC);
CREATE INDEX idx_fraud_rule_conditions_rule_id ON fraud_rule_conditions(fraud_rule_id);
CREATE INDEX idx_fraud_events_created_at ON fraud_events(created_at DESC);
CREATE INDEX idx_fraud_events_customer_id ON fraud_events(customer_id);
CREATE INDEX idx_fraud_events_decision ON fraud_events(decision);
