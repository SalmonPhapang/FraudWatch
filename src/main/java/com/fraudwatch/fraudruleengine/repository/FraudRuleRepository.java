package com.fraudwatch.fraudruleengine.repository;

import com.fraudwatch.fraudruleengine.entity.FraudRule;
import com.fraudwatch.fraudruleengine.entity.RuleStatus;
import com.fraudwatch.fraudruleengine.entity.RuleType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudRuleRepository extends JpaRepository<FraudRule, Long> {

    @Cacheable(value = "fraudRules", key = "'enabled-active'")
    List<FraudRule> findByEnabledTrueAndStatusOrderByPriorityDesc(RuleStatus status);

    @Cacheable(value = "fraudRules", key = "'enabled'")
    List<FraudRule> findByEnabledTrueOrderByPriorityDesc();

    List<FraudRule> findByRuleTypeAndEnabledTrue(RuleType ruleType);
}
