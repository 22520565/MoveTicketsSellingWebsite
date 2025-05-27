package com.movie.main.dto.request;

import java.time.LocalDate;

import com.movie.main.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomerRequestDto(
        @NotBlank @Size(min = User.MinLengthName, max = User.MaxLengthName) String name,
        @NotNull LocalDate birthDate,
        @NotBlank @Email String email,
        @Size(min = User.MinLengthPhoneNumber, max = User.MaxLengthPhoneNumber) String phoneNumber,
        @NotBlank @Size(min = User.MinLengthUsername, max = User.MaxLengthUsername) String username,
        @NotBlank String password) {
}
