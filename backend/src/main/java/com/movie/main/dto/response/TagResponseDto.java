package com.movie.main.dto.response;

public record TagResponseDto(
        Integer id,
        String name)
        implements EntityResponseDtoInterface<Integer> {
}
