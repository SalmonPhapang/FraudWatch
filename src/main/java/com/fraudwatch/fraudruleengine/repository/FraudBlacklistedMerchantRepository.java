package com.fraudwatch.fraudruleengine.repository;

import com.fraudwatch.fraudruleengine.entity.FraudBlacklistedMerchant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudBlacklistedMerchantRepository extends JpaRepository<FraudBlacklistedMerchant, Long> {

    @Cacheable(value = "blacklistedMerchants", key = "#merchantId")
    Optional<FraudBlacklistedMerchant> findByMerchantId(String merchantId);

    boolean existsByMerchantId(String merchantId);
}
