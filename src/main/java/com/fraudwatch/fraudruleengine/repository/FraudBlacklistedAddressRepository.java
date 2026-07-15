package com.fraudwatch.fraudruleengine.repository;

import com.fraudwatch.fraudruleengine.entity.FraudBlacklistedAddress;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudBlacklistedAddressRepository extends JpaRepository<FraudBlacklistedAddress, Long> {

    @Cacheable(value = "blacklistedAddresses", key = "#addressHash")
    Optional<FraudBlacklistedAddress> findByAddressHash(String addressHash);

    boolean existsByAddressHash(String addressHash);
}
