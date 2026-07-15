package com.fraudwatch.fraudruleengine.repository;

import com.fraudwatch.fraudruleengine.entity.FraudBlacklistedCard;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudBlacklistedCardRepository extends JpaRepository<FraudBlacklistedCard, Long> {

    @Cacheable(value = "blacklistedCards", key = "#cardHash")
    Optional<FraudBlacklistedCard> findByCardHash(String cardHash);

    boolean existsByCardHash(String cardHash);
}
