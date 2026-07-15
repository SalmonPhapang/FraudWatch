package com.fraudwatch.fraudruleengine.repository;

import com.fraudwatch.fraudruleengine.entity.RiskThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskThresholdRepository extends JpaRepository<RiskThreshold, Long> {
}
