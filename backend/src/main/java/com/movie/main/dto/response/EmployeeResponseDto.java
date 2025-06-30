package com.movie.main.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.movie.main.entity.Employee.Permission;

public record EmployeeResponseDto(
        int id,
        String name,
        LocalDate birthDate,
        String email,
        String phoneNumber,
        String username,
        String jobTitle,
        int salary,
        LocalTime shiftStart,
        LocalTime shiftEnd,
        LocalDate beginWorkingDate,
        Set<Permission> permissions,
        boolean blocked,
        boolean deleted) {}
