package com.hungrezy.foodordering.repository;

import com.hungrezy.foodordering.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {
    Optional<VerificationCode> findFirstByFlowAndIdentifierOrderByCreatedAtDesc(String flow, String identifier);
    void deleteByFlowAndIdentifier(String flow, String identifier);
}
