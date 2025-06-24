package com.movie.main.dto.internal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record FilmShowEvent(
        long id,
        String roomName,
        String filmName,
        LocalTime startTime,
        int duration,
        List<String> category,
        LocalDate date,
        String description) {}
