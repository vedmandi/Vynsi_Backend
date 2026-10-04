package com.vynsi.dto;

import com.vynsi.model.UserRole;
import com.vynsi.model.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String displayName,
        UserRole role,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}