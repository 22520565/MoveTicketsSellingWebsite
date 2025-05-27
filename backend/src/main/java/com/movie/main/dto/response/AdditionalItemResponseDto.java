package com.movie.main.dto.response;

public record AdditionalItemResponseDto(
        int id,
        String name,
        int price,
        String thumbnailUrl) {
}
