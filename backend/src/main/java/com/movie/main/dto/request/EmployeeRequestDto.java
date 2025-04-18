package com.movie.main.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EmployeeRequestDto(
                @NotNull @Valid UserRequestDto userRequestDto,
                @NotNull String jobTitle,
                @Min(0) int salary,
                @NotNull LocalTime shiftStart,
                @NotNull LocalTime shiftEnd,
                @NotNull LocalDate beginWorkingDate)
                implements UserDetailsRequestDtoInterface {
}
