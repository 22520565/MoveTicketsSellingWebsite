package com.movie.main.dto.response;

import java.time.LocalDate;

public record CustomerResponseDto(
        int id,
        String name,
        LocalDate birthDate,
        String email,
        String phoneNumber,
        String username) {
}
