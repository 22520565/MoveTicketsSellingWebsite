package com.movie.main.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record TokenRefreshRequestDto(
        @NotNull UUID token) {
}
