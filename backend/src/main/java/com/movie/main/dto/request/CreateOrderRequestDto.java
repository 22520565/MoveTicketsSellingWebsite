package com.movie.main.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequestDto(
        int totalPrice,
        int totalPriceAfterDiscount,
        int filmShowId,
        Set<@NotNull TicketDto> tickets,
        Set<@NotNull Integer> seatIds,
        Set<@NotNull ItemDto> items,
        Set<@NotNull Integer> promotionIds,
        int pointUsage) {
    public record TicketDto(int typeId, int quantity) {}

    public record ItemDto(int id, int quantity) {}
}