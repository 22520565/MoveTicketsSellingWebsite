package com.movie.main.dto.response;

import java.util.UUID;

import com.movie.main.dto.InterfaceDto;

public record TokenRefreshResponseDto(String accessToken, UUID refreshToken) implements InterfaceDto {}
