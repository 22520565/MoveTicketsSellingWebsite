package com.movie.main.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.UserRequestDto;
import com.movie.main.dto.response.UserResponseDto;
import com.movie.main.entity.User;
import com.movie.main.repository.UserRepository;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@Service
public class UserService
        extends AbstractEntityService<UserRequestDto, UserResponseDto, User, Integer> {
    @NotNull
    private final UserRepository repository;

    @NotNull
    private final PasswordEncoder passwordEncoder;

    public UserService(
            @NotNull final UserRepository repository,
            @NotNull final PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Nullable
    public User findEntityByUsername(@Nullable final String username) {
        return this.getRepository().findByUsername(username).orElse(null);
    }

    @Nullable
    public UserResponseDto findByUsername(@Nullable final String username) {
        final var user = this.findEntityByUsername(username);
        if (user == null) {
            return null;
        }

        return this.createResponseDtoFromEntity(user);
    }

    public boolean existsByUsername(@Nullable final String username) {
        return this.getRepository().existsByUsername(username);
    }

    @Override
    protected UserResponseDto createResponseDtoFromEntity(@NotNull final User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getBirthDate(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getUsername());
    }

    @Override
    protected User createEntityFromRequestDto(@NotNull final UserRequestDto requestDto) {
        return new User(
                requestDto.name(),
                requestDto.birthDate(),
                requestDto.email(),
                requestDto.phoneNumber(),
                requestDto.username(),
                this.passwordEncoder.encode(requestDto.password()));
    }

    @Override
    protected User updateEntityFromRequestDto(
            @NotNull final User user,
            @NotNull final UserRequestDto requestDto) {
        user.setName(requestDto.name());
        user.setBirthDate(requestDto.birthDate());
        user.setEmail(requestDto.email());
        user.setPhoneNumber(requestDto.phoneNumber());
        user.setUsername(requestDto.username());
        user.setHashedPassword(this.passwordEncoder.encode(requestDto.password()));

        return user;
    }

    @Override
    protected UserRepository getRepository() {
        return this.repository;
    }
}
