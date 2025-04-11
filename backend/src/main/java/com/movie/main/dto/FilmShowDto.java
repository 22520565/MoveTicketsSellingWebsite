package com.movie.main.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public record FilmShowDto(
                int filmId,
                int roomSeatId,
                LocalDateTime showTime,
                @NotBlank String type)
                implements InterfaceDto {
}
