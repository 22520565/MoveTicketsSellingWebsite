package com.movie.main.dto.response;

import java.time.LocalDateTime;

public record FilmShowResponseDto(Integer id, int filmId, int roomId, LocalDateTime showTime, String type)
        implements EntityResponseDtoInterface<Integer> {}
