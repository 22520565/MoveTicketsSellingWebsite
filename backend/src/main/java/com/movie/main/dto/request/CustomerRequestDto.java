package com.movie.main.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CustomerRequestDto(
                @NotNull @Valid UserRequestDto userRequestDto)
                implements UserDetailsRequestDtoInterface {
}
