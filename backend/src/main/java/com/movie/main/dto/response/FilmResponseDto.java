package com.movie.main.dto.response;

import java.time.LocalDate;
import java.util.Set;

public record FilmResponseDto(Integer id, String name, String thumbnailUrl, String trailerUrl, Set<Integer> tagIds,
        int duration, String ageRestriction, String voice, String originatedCountry, boolean is3D, String description,
        String content, LocalDate beginDate) {

}
