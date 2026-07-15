
package com.fraudwatch.fraudruleengine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudwatch.fraudruleengine.dto.FraudCheckRequest;
import com.fraudwatch.fraudruleengine.dto.FraudCheckResponse;
import com.fraudwatch.fraudruleengine.entity.*;
import com.fraudwatch.fraudruleengine.repository.FraudEventRepository;
import com.fraudwatch.fraudruleengine.repository.FraudRuleRepository;
import com.fraudwatch.fraudruleengine.ruleengine.RuleEvaluator;
import com.fraudwatch.fraudruleengine.riskscore.RiskScoringEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudCheckServiceTest {

    @Mock
    private FraudRuleRepository fraudRuleRepository;

    @Mock
    private RuleEvaluator ruleEvaluator;

    @Mock
    private RiskScoringEngine riskScoringEngine;

    @Mock
    private FraudEventRepository fraudEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private SanitizerService sanitizerService;

    @InjectMocks
    private FraudCheckService fraudCheckService;

    @Test
    void testCheckFraud_WhenIdempotencyKeyPresentAndCached_ReturnsCachedResponse() {
        String idempotencyKey = "test-key";
        FraudCheckResponse cachedResponse = new FraudCheckResponse(
                50,
                FraudAction.REVIEW,
                List.of(),
                List.of(),
                100L,
                "Test",
                List.of()
        );

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("idempotency:" + idempotencyKey)).thenReturn(cachedResponse);

        FraudCheckResponse result = fraudCheckService.checkFraud(idempotencyKey, new FraudCheckRequest());

        assertEquals(cachedResponse, result);
        verifyNoInteractions(fraudRuleRepository, ruleEvaluator, riskScoringEngine, fraudEventRepository, sanitizerService);
    }

    @Test
    void testCheckFraud_WhenRulesMatch_ReturnsCorrectResponse() {
        FraudCheckRequest request = new FraudCheckRequest();
        request.setSessionId("test-session");

        FraudRule activeRule = new FraudRule();
        activeRule.setName("Test Rule");
        activeRule.setStatus(RuleStatus.ACTIVE);
        activeRule.setSeverity(100);
        activeRule.setEnabled(true);

        when(fraudRuleRepository.findByEnabledTrueAndStatusOrderByPriorityDesc(RuleStatus.ACTIVE))
                .thenReturn(List.of(activeRule));
        when(fraudRuleRepository.findByEnabledTrueAndStatusOrderByPriorityDesc(RuleStatus.SHADOW))
                .thenReturn(List.of());
        when(ruleEvaluator.evaluateAll(List.of(activeRule), request)).thenReturn(List.of(activeRule));
        when(riskScoringEngine.calculateRiskScore(any())).thenReturn(75);
        when(riskScoringEngine.determineDecision(eq(75), any())).thenReturn(FraudAction.BLOCK);
        when(riskScoringEngine.getRecommendation(FraudAction.BLOCK)).thenReturn("Blocked");

        FraudCheckResponse result = fraudCheckService.checkFraud(null, request);

        assertNotNull(result);
        assertEquals(75, result.getRiskScore());
        assertEquals(FraudAction.BLOCK, result.getDecision());
        assertEquals("Blocked", result.getRecommendation());
        assertTrue(result.getMatchedRules().contains("Test Rule"));
        verify(fraudEventRepository, times(1)).save(any());
    }

    @Test
    void testCheckFraudFallback_ReturnsAllowResponse() {
        Exception ex = new RuntimeException("Test exception");
        FraudCheckResponse result = fraudCheckService.checkFraudFallback(null, new FraudCheckRequest(), ex);

        assertNotNull(result);
        assertEquals(0, result.getRiskScore());
        assertEquals(FraudAction.ALLOW, result.getDecision());
    }
}
