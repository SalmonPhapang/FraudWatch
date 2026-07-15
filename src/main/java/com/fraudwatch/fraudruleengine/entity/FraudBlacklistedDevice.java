package com.fraudwatch.fraudruleengine.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_blacklisted_devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete
public class FraudBlacklistedDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, unique = true, length = 100)
    private String deviceId;

    @Column(length = 255)
    private String fingerprint;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
