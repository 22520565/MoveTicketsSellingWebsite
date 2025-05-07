package com.movie.main.dto.response;

import com.movie.main.entity.FilmTagId;

public record FilmTagReponseDto(FilmTagId id) implements EntityResponseDtoInterface<FilmTagId> {

}
