package com.fraudwatch.fraudruleengine.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "fraud_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "customer_id", length = 100)
    private String customerId;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "merchant_id", length = 100)
    private String merchantId;

    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudAction decision;

    @Type(JsonType.class)
    @Column(name = "request_data", columnDefinition = "jsonb")
    private Map<String, Object> requestData;

    @Column(name = "matched_rules", columnDefinition = "text[]")
    private List<String> matchedRules;

    @Column(name = "reason_codes", columnDefinition = "text[]")
    private List<String> reasonCodes;

    @Column(name = "evaluation_time_ms")
    private Long evaluationTimeMs;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
