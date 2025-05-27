package com.movie.main.dto.response;

public record OrderItemResponseDto(
        int id,
        String name,
        int quantity,
        int price) {
}
