package com.movie.main.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.movie.main.entity.Employee.Permission;
import com.movie.main.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmployeeRequestDto(
        @NotBlank @Size(min = User.MinLengthName, max = User.MaxLengthName) String name,
        @NotNull LocalDate birthDate,
        @NotBlank @Email String email,
        @Size(min = User.MinLengthPhoneNumber, max = User.MaxLengthPhoneNumber) String phoneNumber,
        @NotBlank @Size(min = User.MinLengthUsername, max = User.MaxLengthUsername) String username,
        @NotNull String jobTitle,
        @Min(0) int salary,
        @NotNull LocalTime shiftStart,
        @NotNull LocalTime shiftEnd,
        @NotNull LocalDate beginWorkingDate,
        @NotEmpty Set<Permission> permissions) {
}
