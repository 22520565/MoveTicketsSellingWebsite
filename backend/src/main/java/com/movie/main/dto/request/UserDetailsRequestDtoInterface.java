package com.movie.main.dto.request;

import jakarta.validation.constraints.NotNull;

public interface UserDetailsRequestDtoInterface
        extends EntityRequestDtoInterface {
    @NotNull
    UserRequestDto userRequestDto();
}
