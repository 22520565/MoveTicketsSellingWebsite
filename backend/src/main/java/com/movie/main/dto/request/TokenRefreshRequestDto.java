package com.movie.main.dto.request;

import java.util.UUID;

import com.movie.main.dto.InterfaceDto;

import jakarta.validation.constraints.NotNull;

public record TokenRefreshRequestDto(@NotNull UUID token) implements InterfaceDto {}
