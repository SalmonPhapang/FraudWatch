package com.fraudwatch.fraudruleengine.repository;

import com.fraudwatch.fraudruleengine.entity.FraudAction;
import com.fraudwatch.fraudruleengine.entity.FraudEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FraudEventRepository extends JpaRepository<FraudEvent, Long> {

    Page<FraudEvent> findByCustomerId(String customerId, Pageable pageable);

    List<FraudEvent> findByDecision(FraudAction decision);

    @Query("SELECT fe FROM FraudEvent fe WHERE fe.createdAt BETWEEN :start AND :end")
    List<FraudEvent> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT fe.decision, COUNT(fe) FROM FraudEvent fe GROUP BY fe.decision")
    List<Object[]> countByDecision();

    @Query("SELECT AVG(fe.riskScore) FROM FraudEvent fe")
    Double getAverageRiskScore();
}
