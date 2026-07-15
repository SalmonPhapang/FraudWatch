package com.fraudwatch.fraudruleengine.dto;

import com.fraudwatch.fraudruleengine.entity.Operator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudRuleConditionDTO {

    private Long id;

    @NotBlank
    private String field;

    @NotNull
    private Operator operator;

    private String value;

    private String valueMin;

    private String valueMax;

    private LocalDateTime createdAt;
}
