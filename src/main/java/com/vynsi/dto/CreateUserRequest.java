package com.vynsi.dto;

import com.vynsi.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Display name is required")
        String displayName,

        @NotNull(message = "Role is required")
        UserRole role

) {
}