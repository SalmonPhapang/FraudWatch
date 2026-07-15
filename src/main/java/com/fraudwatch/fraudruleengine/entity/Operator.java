package com.fraudwatch.fraudruleengine.entity;

public enum Operator {
    EQUALS("="),
    NOT_EQUALS("!="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_THAN_OR_EQUAL("<="),
    GREATER_THAN_OR_EQUAL(">="),
    BETWEEN("BETWEEN"),
    IN("IN"),
    NOT_IN("NOT_IN"),
    CONTAINS("CONTAINS"),
    STARTS_WITH("STARTS_WITH"),
    ENDS_WITH("ENDS_WITH"),
    REGEX("REGEX"),
    VELOCITY_GT("VELOCITY_GT"),
    VELOCITY_LT("VELOCITY_LT"),
    VELOCITY_GTE("VELOCITY_GTE"),
    VELOCITY_LTE("VELOCITY_LTE"),
    VELOCITY_EQ("VELOCITY_EQ");

    private final String value;

    Operator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Operator fromValue(String value) {
        for (Operator op : values()) {
            if (op.value.equals(value)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown operator: " + value);
    }
}
