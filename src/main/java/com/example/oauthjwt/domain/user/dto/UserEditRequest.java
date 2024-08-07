package com.example.oauthjwt.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserEditRequest(
        @NotBlank String name,
        @NotBlank String phoneNumber
) {
}
