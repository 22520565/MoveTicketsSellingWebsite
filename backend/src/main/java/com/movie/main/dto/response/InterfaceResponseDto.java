package com.movie.main.dto.response;

import com.movie.main.dto.InterfaceDto;

import jakarta.validation.constraints.NotNull;

public interface InterfaceResponseDto<TKey> extends InterfaceDto {
    @NotNull
    TKey id();
}
