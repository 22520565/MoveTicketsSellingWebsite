package com.movie.main.dto.response;

public record OrderDecoratorsPointUsageResponseDto(
        int customerOrderId,
        int pointUsed,
        int pointToMoneyRatio) {
}
