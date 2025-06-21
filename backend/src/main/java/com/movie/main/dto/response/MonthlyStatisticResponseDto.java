package com.movie.main.dto.response;

public record MonthlyStatisticResponseDto(
        int month,
        long totalNetRevenue,
        long totalEffectiveRevenue) {}
