package com.movie.main.request;

import com.movie.main.entity.Theater;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TheaterCreationRequest(
        @NotBlank @Size(min = Theater.MinLengthName, max = Theater.MaxLengthName) String name,
        @NotBlank @Size(min = Theater.MinLengthAddress, max = Theater.MaxLengthAddress) String address) {
}
