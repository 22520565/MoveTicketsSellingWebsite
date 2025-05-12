package com.movie.main.dto.response;

import java.time.LocalDate;
import java.util.Set;

import com.movie.main.entity.Tag;

public record FilmResponseDto(Integer id, String name, String thumbnailUrl, String trailerUrl, Set<Tag> tags,
        int duration, String ageRestriction, String voice, String originatedCountry, boolean is3D, String description,
        String content, LocalDate beginDate) {

}
