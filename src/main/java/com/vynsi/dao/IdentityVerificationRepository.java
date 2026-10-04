package com.vynsi.dao;

import com.vynsi.entity.IdentityVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IdentityVerificationRepository
        extends JpaRepository<IdentityVerification, UUID> {

    List<IdentityVerification> findByTalent_Id(UUID talentId);

    Optional<IdentityVerification>
    findTopByTalent_IdOrderByCreatedAtDesc(UUID talentId);
}