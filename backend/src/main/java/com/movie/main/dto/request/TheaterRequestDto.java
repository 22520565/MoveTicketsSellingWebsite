package com.movie.main.dto.request;

import com.movie.main.entity.Theater;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TheaterRequestDto(
        @NotBlank @Size(min = Theater.MinLengthName, max = Theater.MaxLengthName) String name,
        @NotBlank @Size(min = Theater.MinLengthAddress, max = Theater.MaxLengthAddress) String address,
        @NotBlank @Size(min = Theater.MinLengthCity, max = Theater.MaxLengthCity) String city) {
}
