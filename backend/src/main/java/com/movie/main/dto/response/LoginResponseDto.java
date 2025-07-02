package com.movie.main.dto.response;

import java.util.UUID;

public record LoginResponseDto(
        String accessToken,
        UUID refreshToken) {
}
