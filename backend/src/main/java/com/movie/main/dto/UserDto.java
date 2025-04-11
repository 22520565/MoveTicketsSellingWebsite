package com.movie.main.dto;

import java.sql.Date;

import com.movie.main.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDto(
        @NotBlank @Size(min = User.MinLengthName, max = User.MaxLengthName) String name,
        @NotNull Date birthDate,
        @NotBlank @Email String email,
        @Size(min = User.MinLengthPhoneNumber, max = User.MaxLengthPhoneNumber) String phoneNumber,
        @Size(min = User.MinLengthUsername, max = User.MaxLengthUsername) String username,
        @NotBlank String plainPassword)
        implements InterfaceDto {
}
