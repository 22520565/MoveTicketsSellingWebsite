package com.movie.main.dto.response;

import jakarta.validation.constraints.NotNull;

public interface UserDetailsResponseDtoInterface
        extends EntityResponseDtoInterface<Integer> {
    @NotNull
    UserResponseDto userResponseDto();

    @Override
    @NotNull
    default Integer id() {
        return this.userResponseDto().id();
    }
}
