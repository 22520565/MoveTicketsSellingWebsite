package com.movie.main.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.movie.main.entity.Employee.Permission;

public record EmployeeResponseDto(UserResponseDto userResponseDto, String jobTitle, int salary, LocalTime shiftStart,
        LocalTime shiftEnd, LocalDate beginWorkingDate, Set<Permission> permissions)
        implements UserDetailsResponseDtoInterface {}
