package com.fraudwatch.fraudruleengine.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fraud_rule_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudRuleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fraud_rule_id", nullable = false)
    private FraudRule fraudRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_group_id")
    private FraudRuleGroup parentGroup;

    @OneToMany(mappedBy = "parentGroup")
    private List<FraudRuleGroup> childGroups;

    @OneToMany(mappedBy = "group")
    private List<FraudRuleCondition> conditions;

    @Enumerated(EnumType.STRING)
    @Column(name = "logical_operator", nullable = false)
    private LogicalOperator logicalOperator;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
