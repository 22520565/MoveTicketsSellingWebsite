package com.movie.main.dto.response;

import java.time.LocalDate;

public record UserResponseDto(
        Integer id,
        String name,
        LocalDate birthDate,
        String email,
        String phoneNumber,
        String username)
        implements EntityResponseDtoInterface<Integer> {
}
