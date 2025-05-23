package com.movie.main.dto.response;

import java.util.Set;

import com.movie.main.entity.Promotion;

import jakarta.validation.constraints.NotNull;

public record OrderDecoratorsPromotionResponseDto(int customerOrderId, @NotNull Set<@NotNull Promotion> promotions) {}
