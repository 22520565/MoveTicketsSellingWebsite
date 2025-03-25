package com.movie.main.request;

import com.movie.main.entity.Movie;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

public record MovieUpdateRequest(
        @Size(min = Movie.MinLengthName, max = Movie.MaxLengthName) String name,
        @Size(min = Movie.MinLengthDescription, max = Movie.MaxLengthDescription) String description) {
    @AssertTrue
    public boolean isValid() {
        return (this.name != null) || (this.description != null);
    }
}
