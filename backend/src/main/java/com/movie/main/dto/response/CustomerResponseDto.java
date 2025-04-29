package com.movie.main.dto.response;

import jakarta.validation.constraints.NotNull;

public record CustomerResponseDto(
                @NotNull UserResponseDto userResponseDto)
                implements UserDetailsResponseDtoInterface {
}
