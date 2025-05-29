package com.movie.main.dto.response;

public record TicketTypeResponseDto(
        int id,
        String title,
        int price,
        boolean isPair) {}
