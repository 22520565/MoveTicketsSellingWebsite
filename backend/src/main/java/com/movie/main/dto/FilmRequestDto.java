package com.movie.main.dto;

import java.sql.Date;

import com.movie.main.entity.Film;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FilmRequestDto(
                @NotBlank @Size(min = Film.MinLengthName, max = Film.MaxLengthName) String name,
                @NotBlank String thumbnailUrl,
                @NotBlank String trailerUrl,
                int tagId,
                @Min(1) int duration,
                @NotBlank String ageRestriction,
                @NotBlank String voice,
                @NotBlank String originatedCountry,
                boolean is3D,
                @NotBlank @Size(min = Film.MinLengthDescription, max = Film.MaxLengthDescription) String description,
                @NotBlank String content,
                @NotNull Date beginDate)
                implements InterfaceDto {
}
