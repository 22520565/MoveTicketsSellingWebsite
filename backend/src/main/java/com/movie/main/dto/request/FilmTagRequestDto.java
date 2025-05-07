package com.movie.main.dto.request;

import com.movie.main.entity.FilmTagId;

public record FilmTagRequestDto(FilmTagId id) implements EntityRequestDtoInterface {}
