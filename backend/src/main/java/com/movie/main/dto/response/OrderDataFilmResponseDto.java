package com.movie.main.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.movie.main.entity.OrderTicket;

import jakarta.validation.constraints.NotNull;

public record OrderDataFilmResponseDto(
        int customerOrderId,
        String filmName,
        String ageRestriction,
        LocalDate date,
        LocalTime time,
        String verifyCode,
        String roomName,
        Set<@NotNull String> seatNames,
        Set<@NotNull OrderTicket> tickets) {
}
