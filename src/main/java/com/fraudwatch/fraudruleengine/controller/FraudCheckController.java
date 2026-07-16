
package com.fraudwatch.fraudruleengine.controller;

import com.fraudwatch.fraudruleengine.dto.FraudCheckRequest;
import com.fraudwatch.fraudruleengine.dto.FraudCheckResponse;
import com.fraudwatch.fraudruleengine.kafka.TransactionKafkaProducer;
import com.fraudwatch.fraudruleengine.service.FraudCheckService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/fraud/check")
@Tag(name = "Fraud Check", description = "Fraud detection endpoints")
public class FraudCheckController {

    private final FraudCheckService fraudCheckService;
    private final TransactionKafkaProducer transactionKafkaProducer;

    public FraudCheckController(FraudCheckService fraudCheckService,
                                 TransactionKafkaProducer transactionKafkaProducer) {
        this.fraudCheckService = fraudCheckService;
        this.transactionKafkaProducer = transactionKafkaProducer;
    }

    @PostMapping
    @Operation(summary = "Check transaction for fraud")
    @RateLimiter(name = "fraud-check")
    public ResponseEntity<FraudCheckResponse> checkFraud(
            @RequestHeader(name = "Idempotency-Key", required = true) String idempotencyKey,
            @Valid @RequestBody FraudCheckRequest request) {
        // First, send to Kafka for async processing (if needed)
        transactionKafkaProducer.sendTransaction(request);
        // Then process synchronously for immediate response
        FraudCheckResponse response = fraudCheckService.checkFraud(idempotencyKey, request);
        return ResponseEntity.ok(response);
    }
}

