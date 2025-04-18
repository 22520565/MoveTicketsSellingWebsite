package com.movie.main.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record EmployeeResponseDto(
                UserResponseDto userResponseDto,
                String jobTitle,
                int salary,
                LocalTime shiftStart,
                LocalTime shiftEnd,
                LocalDate beginWorkingDate)
                implements UserDetailsResponseDtoInterface {
}
