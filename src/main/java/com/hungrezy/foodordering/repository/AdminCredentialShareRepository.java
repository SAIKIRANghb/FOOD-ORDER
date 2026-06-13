package com.hungrezy.foodordering.repository;

import com.hungrezy.foodordering.entity.AdminCredentialShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminCredentialShareRepository extends JpaRepository<AdminCredentialShare, UUID> {
}
