package com.movie.main.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SetPasswordRequestDto(@NotBlank String password) {}
