package com.movie.main.dto.response;

import java.util.UUID;

public record LoginResponseDto(int userId, String accessToken, UUID refreshToken) {}
