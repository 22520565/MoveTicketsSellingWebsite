package com.movie.main.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.movie.main.entity.FilmShow;
import com.movie.main.entity.OrderTicket;
import com.movie.main.entity.RoomSeat;

import jakarta.validation.constraints.NotNull;

public record OrderDataFilmResponseDto(
        int customerOrderId,
        LocalDate date,
        LocalTime time,
        FilmShow filmShow,
        Set<@NotNull RoomSeat> roomSeats,
        Set<@NotNull OrderTicket> orderTickets) {}
