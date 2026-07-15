package com.fraudwatch.fraudruleengine.repository;

import com.fraudwatch.fraudruleengine.entity.FraudScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudScoreRepository extends JpaRepository<FraudScore, Long> {

    Page<FraudScore> findByCustomerId(String customerId, Pageable pageable);
}
