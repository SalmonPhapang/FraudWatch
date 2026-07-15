package com.fraudwatch.fraudruleengine.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fraud_rule_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudRuleGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fraud_rule_id", nullable = false)
    @JsonIgnore
    private FraudRule fraudRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_group_id")
    @JsonIgnore
    private FraudRuleGroup parentGroup;

    @OneToMany(mappedBy = "parentGroup")
    @JsonIgnore
    private List<FraudRuleGroup> childGroups;

    @OneToMany(mappedBy = "group")
    @JsonIgnore
    private List<FraudRuleCondition> conditions;

    @Enumerated(EnumType.STRING)
    @Column(name = "logical_operator", nullable = false)
    private LogicalOperator logicalOperator;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
