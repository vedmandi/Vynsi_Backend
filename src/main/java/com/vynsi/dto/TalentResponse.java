package com.vynsi.dto;

import com.vynsi.model.TalentStatus;

import java.time.Instant;
import java.util.UUID;

public record TalentResponse(

        UUID id,

        UUID userId,

        String email,

        String displayName,

        String legalName,

        String stageName,

        String biography,

        String countryCode,

        TalentStatus status,

        Instant createdAt,

        Instant updatedAt

) {
}