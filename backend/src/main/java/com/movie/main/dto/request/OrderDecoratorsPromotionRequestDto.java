package com.movie.main.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record OrderDecoratorsPromotionRequestDto(
        int customerOrderId,
        @NotNull Set<@NotNull Integer> promotionIds) {}
