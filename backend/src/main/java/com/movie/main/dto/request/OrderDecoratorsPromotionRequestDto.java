package com.movie.main.dto.request;

import java.util.Set;

import com.movie.main.entity.Promotion;

import jakarta.validation.constraints.NotNull;

public record OrderDecoratorsPromotionRequestDto(
        int customerOrderId,
        @NotNull Set<@NotNull Promotion> promotions) {
}
