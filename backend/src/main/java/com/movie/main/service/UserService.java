package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.entity.User;
import com.movie.main.repository.UserRepository;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
    @NotNull
    private final UserRepository repository;

    public UserService(@NotNull final UserRepository repository) {
        this.repository = repository;
    }

    @Nullable
    public User findByUsernameAndDeletedFalse(@Nullable final String username) {
        return this.repository.findByUsernameAndDeletedFalse(username).orElse(null);
    }

    @Nullable
    public User findByUsername(@Nullable final String username) {
        return this.repository.findByUsername(username).orElse(null);
    }
}
