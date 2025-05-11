package com.movie.main.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record FilmShowResponseDto(Integer id, int filmId, int roomId, LocalDate showDate, LocalTime showTime,
        String type) {}
