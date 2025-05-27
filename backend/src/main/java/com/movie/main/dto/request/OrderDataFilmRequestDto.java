package com.movie.main.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.movie.main.entity.OrderTicket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderDataFilmRequestDto(
        int customerOrderId,
        @NotBlank String filmName,
        @NotBlank String ageRestriction,
        @NotNull LocalDate date,
        @NotNull LocalTime time,
        String verifyCode,
        String roomName,
        @NotNull Set<@NotNull String> seatNames,
        @NotNull Set<@NotNull OrderTicket> tickets) {
}
