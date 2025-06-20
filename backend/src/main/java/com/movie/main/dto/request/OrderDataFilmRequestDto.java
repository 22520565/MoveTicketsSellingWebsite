package com.movie.main.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record OrderDataFilmRequestDto(
        int customerOrderId,
        @NotNull LocalDate date,
        @NotNull LocalTime time,
        int filmShowId,
        String verifyCode,
        @NotNull Set<@NotNull Integer> roomSeatIds,
        @NotNull Set<@NotNull Integer> orderTicketIds) {}
