
package com.fraudwatch.fraudruleengine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudwatch.fraudruleengine.dto.FraudCheckRequest;
import com.fraudwatch.fraudruleengine.dto.FraudCheckResponse;
import com.fraudwatch.fraudruleengine.entity.FraudEvent;
import com.fraudwatch.fraudruleengine.entity.FraudRule;
import com.fraudwatch.fraudruleengine.entity.RuleStatus;
import com.fraudwatch.fraudruleengine.repository.FraudEventRepository;
import com.fraudwatch.fraudruleengine.repository.FraudRuleRepository;
import com.fraudwatch.fraudruleengine.ruleengine.RuleEvaluator;
import com.fraudwatch.fraudruleengine.riskscore.RiskScoringEngine;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FraudCheckService {

    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:";

    private final FraudRuleRepository fraudRuleRepository;
    private final RuleEvaluator ruleEvaluator;
    private final RiskScoringEngine riskScoringEngine;
    private final FraudEventRepository fraudEventRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SanitizerService sanitizerService;

    public FraudCheckService(FraudRuleRepository fraudRuleRepository,
                              RuleEvaluator ruleEvaluator,
                              RiskScoringEngine riskScoringEngine,
                              FraudEventRepository fraudEventRepository,
                              ObjectMapper objectMapper,
                              RedisTemplate<String, Object> redisTemplate,
                              SanitizerService sanitizerService) {
        this.fraudRuleRepository = fraudRuleRepository;
        this.ruleEvaluator = ruleEvaluator;
        this.riskScoringEngine = riskScoringEngine;
        this.fraudEventRepository = fraudEventRepository;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
        this.sanitizerService = sanitizerService;
    }

    @Transactional
    @CircuitBreaker(name = "fraudCheck", fallbackMethod = "checkFraudFallback")
    @RateLimiter(name = "fraudCheck")
    public FraudCheckResponse checkFraud(String idempotencyKey, FraudCheckRequest request) {
        // Check idempotency key first
        if (StringUtils.isNotBlank(idempotencyKey)) {
            String key = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
            FraudCheckResponse cachedResponse = (FraudCheckResponse) redisTemplate.opsForValue().get(key);
            if (cachedResponse != null) {
                return cachedResponse;
            }
        }

        // Sanitize input to prevent XSS
        sanitizerService.sanitize(request);

        long startTime = System.currentTimeMillis();

        // Fetch active rules
        List<FraudRule> activeRules = fraudRuleRepository.findByEnabledTrueAndStatusOrderByPriorityDesc(RuleStatus.ACTIVE);
        List<FraudRule> matchedActiveRules = ruleEvaluator.evaluateAll(activeRules, request);

        // Fetch and evaluate shadow rules (don't affect decision)
        List<FraudRule> shadowRules = fraudRuleRepository.findByEnabledTrueAndStatusOrderByPriorityDesc(RuleStatus.SHADOW);
        List<FraudRule> matchedShadowRules = ruleEvaluator.evaluateAll(shadowRules, request);
        if (!matchedShadowRules.isEmpty()) {
            log.info("Shadow rules matched for session {}: {}", 
                request.getSessionId(), 
                matchedShadowRules.stream().map(FraudRule::getName).collect(Collectors.toList()));
        }

        int riskScore = riskScoringEngine.calculateRiskScore(matchedActiveRules);
        var decision = riskScoringEngine.determineDecision(riskScore, matchedActiveRules);
        String recommendation = riskScoringEngine.getRecommendation(decision);

        long evaluationTime = System.currentTimeMillis() - startTime;

        List<String> matchedRuleNames = matchedActiveRules.stream()
                .map(FraudRule::getName)
                .collect(Collectors.toList());
        
        List<String> shadowMatchedRuleNames = matchedShadowRules.stream()
                .map(FraudRule::getName)
                .collect(Collectors.toList());

        FraudEvent event = new FraudEvent();
        event.setSessionId(request.getSessionId());
        event.setCustomerId(request.getCustomer() != null ? request.getCustomer().getId() : null);
        event.setDeviceId(request.getDevice() != null ? request.getDevice().getId() : null);
        event.setMerchantId(request.getMerchant() != null ? request.getMerchant().getId() : null);
        event.setRiskScore(riskScore);
        event.setDecision(decision);
        event.setRequestData(objectMapper.convertValue(request, Map.class));
        event.setMatchedRules(matchedRuleNames);
        event.setReasonCodes(matchedRuleNames);
        event.setEvaluationTimeMs(evaluationTime);
        fraudEventRepository.save(event);

        FraudCheckResponse response = new FraudCheckResponse(
                riskScore,
                decision,
                matchedRuleNames,
                matchedRuleNames,
                evaluationTime,
                recommendation,
                shadowMatchedRuleNames
        );

        // Cache response if idempotency key is present
        if (StringUtils.isNotBlank(idempotencyKey)) {
            String key = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
            redisTemplate.opsForValue().set(key, response, Duration.ofHours(24));
        }

        return response;
    }
    
    // Fallback method for circuit breaker/rate limiter
    public FraudCheckResponse checkFraudFallback(String idempotencyKey, FraudCheckRequest request, Exception ex) {
        log.warn("Fallback triggered for fraud check: {}", ex.getMessage());
        // In fallback mode, we can either allow the transaction (fail-open) or block (fail-closed)
        // Here we'll fail-open for safety, but this can be adjusted based on business needs
        return new FraudCheckResponse(
                0,
                com.fraudwatch.fraudruleengine.entity.FraudAction.ALLOW,
                List.of(),
                List.of(),
                0L,
                "Transaction approved - system in fallback mode",
                List.of()
        );
    }
}

