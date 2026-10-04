package com.vynsi.dao;

import com.vynsi.entity.TalentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TalentProfileRepository
        extends JpaRepository<TalentProfile, UUID> {

    Optional<TalentProfile> findByUser_Id(UUID userId);

    boolean existsByUser_Id(UUID userId);
}