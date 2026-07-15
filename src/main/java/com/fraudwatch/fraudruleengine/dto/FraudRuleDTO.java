package com.fraudwatch.fraudruleengine.dto;

import com.fraudwatch.fraudruleengine.entity.FraudAction;
import com.fraudwatch.fraudruleengine.entity.RuleStatus;
import com.fraudwatch.fraudruleengine.entity.RuleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudRuleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank
    private String name;

    private String description;

    private Boolean enabled = true;

    private RuleStatus status = RuleStatus.ACTIVE;

    private Integer priority = 0;

    private Integer severity = 0;

    private Integer weight = 0;

    @NotNull
    private FraudAction action;

    @NotNull
    private RuleType ruleType;

    private List<FraudRuleConditionDTO> conditions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
