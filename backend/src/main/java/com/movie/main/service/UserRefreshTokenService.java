package com.movie.main.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.movie.main.entity.User;
import com.movie.main.entity.UserRefreshToken;
import com.movie.main.repository.UserRefreshTokenRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

@Service
public class UserRefreshTokenService {
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(7);

    @NotNull
    private final UserRefreshTokenRepository repository;

    @NotNull
    private final UserService userService;

    public UserRefreshTokenService(@NotNull final UserRefreshTokenRepository repository,
            @NotNull final UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public UserRefreshToken createRefreshToken(@NotNull final User user) {
        final var newUserRefreshToken = new UserRefreshToken(user);
        while (this.repository.existsByRefreshToken(newUserRefreshToken.getRefreshToken())) {
            newUserRefreshToken.setRefreshToken(UUID.randomUUID());
        }

        return this.repository.save(newUserRefreshToken);
    }

    @Transactional
    public UserRefreshToken createRefreshToken(@NotNull final UserRefreshToken oldUserRefreshToken) {
        this.deleteByRefreshToken(oldUserRefreshToken.getRefreshToken());
        return this.createRefreshToken(oldUserRefreshToken.getUser());
    }

    public UserRefreshToken findByRefreshToken(final UUID refreshToken) {
        return this.repository.findByRefreshToken(refreshToken).orElse(null);
    }

    public boolean isUserRefreshTokenValid(final UserRefreshToken userRefreshToken) {
        if (userRefreshToken.getExpiryDate().isBefore(Instant.now())) {
            this.repository.delete(userRefreshToken);
            return false;
        }

        return true;
    }

    @Transactional
    public void deleteByRefreshToken(final UUID refreshToken) {
        this.repository.deleteByRefreshToken(refreshToken);
    }

    @Scheduled(fixedDelay = 7, timeUnit = TimeUnit.DAYS)
    @Transactional
    public void cleanExpiredRefreshTokenEntities() {
        this.repository.deleteAllByExpiryDateBefore(Instant.now());
    }
}
