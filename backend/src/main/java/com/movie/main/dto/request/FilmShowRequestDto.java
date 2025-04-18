package com.movie.main.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public record FilmShowRequestDto(
                int filmId,
                int roomSeatId,
                LocalDateTime showTime,
                @NotBlank String type)
                implements EntityRequestDtoInterface {
}
