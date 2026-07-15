package com.fraudwatch.fraudruleengine.repository;

import com.fraudwatch.fraudruleengine.entity.FraudBlacklistedDevice;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudBlacklistedDeviceRepository extends JpaRepository<FraudBlacklistedDevice, Long> {

    @Cacheable(value = "blacklistedDevices", key = "#deviceId")
    Optional<FraudBlacklistedDevice> findByDeviceId(String deviceId);

    boolean existsByDeviceId(String deviceId);
}
