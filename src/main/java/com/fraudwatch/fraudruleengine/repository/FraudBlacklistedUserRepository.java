package com.fraudwatch.fraudruleengine.repository;

import com.fraudwatch.fraudruleengine.entity.FraudBlacklistedUser;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudBlacklistedUserRepository extends JpaRepository<FraudBlacklistedUser, Long> {

    @Cacheable(value = "blacklistedUsers", key = "#userId")
    Optional<FraudBlacklistedUser> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
