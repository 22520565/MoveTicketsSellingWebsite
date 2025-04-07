package com.movie.main.dto;

import com.movie.main.entity.Movie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MovieDTO(
        @NotBlank @Size(min = Movie.MinLengthName, max = Movie.MaxLengthName) String name,
        @NotBlank @Size(min = Movie.MinLengthDescription, max = Movie.MaxLengthDescription) String description) {
}
