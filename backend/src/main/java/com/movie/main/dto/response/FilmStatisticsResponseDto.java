package com.movie.main.dto.response;

import java.util.List;
import java.util.Set;

import com.movie.main.dto.internal.FilmShowEvent;

public record FilmStatisticsResponseDto(
        Set<String> roomNames,
        List<FilmShowEvent> events) {}
