package com.fraudwatch.fraudruleengine.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_rule_conditions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudRuleCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fraud_rule_id", nullable = false)
    @JsonIgnore
    private FraudRule fraudRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private FraudRuleGroup group;

    @Column(nullable = false, length = 200)
    private String field;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Operator operator;

    @Column(columnDefinition = "TEXT")
    private String value;

    @Column(name = "value_min", columnDefinition = "TEXT")
    private String valueMin;

    @Column(name = "value_max", columnDefinition = "TEXT")
    private String valueMax;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
