package com.movie.main.dto.response;

import java.util.UUID;

public record TokenRefreshResponseDto(String accessToken, UUID refreshToken) {}
