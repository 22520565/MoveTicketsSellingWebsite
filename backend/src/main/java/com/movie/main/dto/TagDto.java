package com.movie.main.dto;

import com.movie.main.entity.Tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagDto(
        @NotBlank @Size(min = Tag.MinLengthName, max = Tag.MaxLengthName) String name)
        implements InterfaceDto {
}
