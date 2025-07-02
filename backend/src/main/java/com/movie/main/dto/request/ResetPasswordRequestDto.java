package com.movie.main.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDto(
        @NotBlank String oldPassword,
        @NotBlank String newPassword) {
}
