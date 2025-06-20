package com.movie.main.dto.response;

public record DailyStatisticResponseDto(
        long totalNetRevenue,
        long totalEffectiveRevenue,
        long totalTicketRevenue) {}
