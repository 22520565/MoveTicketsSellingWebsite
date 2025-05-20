package com.movie.main.dto.internal;

import jakarta.validation.constraints.NotBlank;

public record CloudinaryImage(@NotBlank String url, @NotBlank String publicId) {}
