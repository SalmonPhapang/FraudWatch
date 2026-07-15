
package com.fraudwatch.fraudruleengine.kafka;

import com.fraudwatch.fraudruleengine.dto.FraudCheckRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionKafkaProducer {

    private static final String TOPIC = "fraud-transactions";
    private final KafkaTemplate<String, FraudCheckRequest> kafkaTemplate;

    public TransactionKafkaProducer(KafkaTemplate<String, FraudCheckRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransaction(FraudCheckRequest request) {
        kafkaTemplate.send(TOPIC, request.getSessionId(), request);
    }
}

