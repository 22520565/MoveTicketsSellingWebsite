package com.movie.main.dto.request;

import java.time.LocalDate;
import java.util.Set;

import com.movie.main.entity.Film;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FilmRequestDto(@NotBlank @Size(min = Film.MinLengthName, max = Film.MaxLengthName) String name,
        @NotBlank String thumbnailUrl, @NotBlank String trailerUrl,
        @Size(max = Film.MaxAmountTags) Set<@NotNull Integer> tagIds, @Min(1) int duration,
        @NotBlank String ageRestriction, @NotBlank String voice, @NotBlank String originatedCountry, boolean is3D,
        @NotBlank @Size(min = Film.MinLengthDescription, max = Film.MaxLengthDescription) String description,
        @NotBlank @Size(min = Film.MinLengthContent, max = Film.MaxLengthContent) String content,
        @NotNull LocalDate beginDate) {}
