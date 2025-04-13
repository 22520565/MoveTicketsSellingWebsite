package com.movie.main.dto.response;

public record TheaterResponseDto(
        Integer id,
        String name,
        String address)
        implements InterfaceResponseDto<Integer> {

}
