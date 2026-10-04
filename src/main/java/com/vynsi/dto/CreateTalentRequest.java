package com.vynsi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateTalentRequest(

        @NotNull(message = "User ID is required")
        UUID userId,

        @Size(max = 255)
        String legalName,

        @Size(max = 255)
        String stageName,

        String biography,

        @Size(max = 10)
        String countryCode

) {
}