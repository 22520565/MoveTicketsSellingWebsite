package com.movie.main.dto.request;

import com.movie.main.entity.AgeRestriction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgeRestrictionRequestDto(
        @Size(min = AgeRestriction.MinLengthName, max = AgeRestriction.MaxLengthName) @NotBlank String name) {}
