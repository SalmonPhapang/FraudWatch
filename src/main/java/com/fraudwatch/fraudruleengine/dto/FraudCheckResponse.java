package com.fraudwatch.fraudruleengine.dto;

import com.fraudwatch.fraudruleengine.entity.FraudAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudCheckResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer riskScore;
    private FraudAction decision;
    private List<String> matchedRules;
    private List<String> reasonCodes;
    private Long evaluationTimeMs;
    private String recommendation;
    private List<String> shadowMatchedRules;
}
