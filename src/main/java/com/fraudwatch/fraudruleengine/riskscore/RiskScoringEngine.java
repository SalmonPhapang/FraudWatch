package com.fraudwatch.fraudruleengine.riskscore;

import com.fraudwatch.fraudruleengine.entity.FraudAction;
import com.fraudwatch.fraudruleengine.entity.FraudRule;
import com.fraudwatch.fraudruleengine.entity.RiskThreshold;
import com.fraudwatch.fraudruleengine.repository.RiskThresholdRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RiskScoringEngine {

    private final RiskThresholdRepository riskThresholdRepository;

    @Value("${app.risk.thresholds.allow:30}")
    private int allowThreshold;

    @Value("${app.risk.thresholds.review:70}")
    private int reviewThreshold;

    public RiskScoringEngine(RiskThresholdRepository riskThresholdRepository) {
        this.riskThresholdRepository = riskThresholdRepository;
    }

    public int calculateRiskScore(List<FraudRule> matchedRules) {
        int score = 0;
        for (FraudRule rule : matchedRules) {
            score += rule.getWeight();
        }
        return Math.min(score, 100);
    }

    public FraudAction determineDecision(int riskScore, List<FraudRule> matchedRules) {
        // Rule conflict resolution:
        // 1. Highest priority rule's action takes precedence
        // 2. If multiple rules, action severity order: BLOCK > REVIEW > ALLOW
        if (!matchedRules.isEmpty()) {
            // Sort matched rules by priority descending
            matchedRules.sort((r1, r2) -> r2.getPriority() - r1.getPriority());
            
            // Check for most severe action in matched rules
            FraudAction mostSevereAction = FraudAction.ALLOW;
            for (FraudRule rule : matchedRules) {
                if (rule.getAction() == FraudAction.BLOCK) {
                    mostSevereAction = FraudAction.BLOCK;
                    break; // BLOCK is highest severity, no need to check further
                } else if (rule.getAction() == FraudAction.REVIEW) {
                    mostSevereAction = FraudAction.REVIEW;
                }
            }
            
            // Also consider the highest priority rule's action
            FraudAction highestPriorityAction = matchedRules.get(0).getAction();
            
            // Choose the more severe between highest priority and most severe
            if (highestPriorityAction == FraudAction.BLOCK || mostSevereAction == FraudAction.BLOCK) {
                return FraudAction.BLOCK;
            } else if (highestPriorityAction == FraudAction.REVIEW || mostSevereAction == FraudAction.REVIEW) {
                return FraudAction.REVIEW;
            }
        }
        
        // Fallback to score-based decision
        RiskThreshold threshold = riskThresholdRepository.findById(1L).orElse(null);
        int allowMax = threshold != null ? threshold.getAllowMax() : allowThreshold;
        int reviewMax = threshold != null ? threshold.getReviewMax() : reviewThreshold;

        if (riskScore <= allowMax) {
            return FraudAction.ALLOW;
        } else if (riskScore <= reviewMax) {
            return FraudAction.REVIEW;
        } else {
            return FraudAction.BLOCK;
        }
    }
    
    // Backward compatibility
    public FraudAction determineDecision(int riskScore) {
        return determineDecision(riskScore, List.of());
    }

    public String getRecommendation(FraudAction decision) {
        return switch (decision) {
            case ALLOW -> "Transaction approved - low risk";
            case REVIEW -> "Transaction flagged for manual review - medium risk";
            case BLOCK -> "Transaction blocked - high risk";
        };
    }
}
