
package com.fraudwatch.fraudruleengine.kafka;

import com.fraudwatch.fraudruleengine.dto.FraudCheckRequest;
import com.fraudwatch.fraudruleengine.service.FraudCheckService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionKafkaConsumer {

    private final FraudCheckService fraudCheckService;

    public TransactionKafkaConsumer(FraudCheckService fraudCheckService) {
        this.fraudCheckService = fraudCheckService;
    }

    @KafkaListener(topics = "fraud-transactions", groupId = "fraud-check-group")
    public void consumeTransaction(FraudCheckRequest request) {
        // We can use sessionId as idempotency key
        String idempotencyKey = request.getSessionId();
        if (StringUtils.isBlank(idempotencyKey)) {
            idempotencyKey = null;
        }
        fraudCheckService.checkFraud(idempotencyKey, request);
    }
}

