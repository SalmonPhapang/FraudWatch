package com.fraudwatch.fraudruleengine.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fraud_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete
public class FraudRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleStatus status = RuleStatus.ACTIVE;

    @Column(nullable = false)
    private Integer priority = 0;

    @Column(nullable = false)
    private Integer severity = 0;

    @Column(nullable = false)
    private Integer weight = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false)
    private RuleType ruleType;

    @OneToMany(mappedBy = "fraudRule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FraudRuleCondition> conditions;

    @OneToMany(mappedBy = "fraudRule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FraudRuleGroup> groups;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
