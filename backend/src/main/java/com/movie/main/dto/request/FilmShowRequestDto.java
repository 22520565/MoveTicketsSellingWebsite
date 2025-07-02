package com.movie.main.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;

public record FilmShowRequestDto(
        int filmId,
        int roomId,
        LocalDate showDate,
        LocalTime showTime,
        @NotBlank String type) {
}
