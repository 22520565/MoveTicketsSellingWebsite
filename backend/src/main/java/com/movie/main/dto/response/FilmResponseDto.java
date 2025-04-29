package com.movie.main.dto.response;

import java.sql.Date;

public record FilmResponseDto(
                Integer id,
                String name,
                String thumbnailUrl,
                String trailerUrl,
                int tagId,
                int duration,
                String ageRestriction,
                String voice,
                String originatedCountry,
                boolean is3D,
                String description,
                String content,
                Date beginDate)
                implements EntityResponseDtoInterface<Integer> {

}
