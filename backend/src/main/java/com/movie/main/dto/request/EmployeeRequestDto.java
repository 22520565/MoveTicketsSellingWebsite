package com.movie.main.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.movie.main.entity.Employee.Permission;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record EmployeeRequestDto(@NotNull @Valid UserRequestDto userRequestDto, @NotNull String jobTitle,
        @Min(0) int salary, @NotNull LocalTime shiftStart, @NotNull LocalTime shiftEnd,
        @NotNull LocalDate beginWorkingDate, @NotEmpty Set<Permission> permissions)
        implements UserDetailsRequestDtoInterface {}
