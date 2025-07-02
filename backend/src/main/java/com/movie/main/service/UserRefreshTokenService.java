package com.movie.main.service;

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
    @NotNull
    private final UserRefreshTokenRepository repository;

    public UserRefreshTokenService(@NotNull final UserRefreshTokenRepository repository) {
        this.repository = repository;
    }

    public UserRefreshToken createRefreshToken(
            @NotNull final User user) {
        final var newUserRefreshToken = new UserRefreshToken(user);
        return this.repository.save(newUserRefreshToken);
    }

    @Transactional
    public UserRefreshToken createRefreshToken(
            @NotNull final UserRefreshToken oldUserRefreshToken) {
        this.deleteById(oldUserRefreshToken.getId());
        return this.createRefreshToken(oldUserRefreshToken.getUser());
    }

    public UserRefreshToken findById(final UUID id) {
        return this.repository.findById(id).orElse(null);
    }

    public boolean isUserRefreshTokenValid(final UserRefreshToken userRefreshToken) {
        if (userRefreshToken.getExpiryDate().isBefore(Instant.now())) {
            this.repository.delete(userRefreshToken);
            return false;
        }

        return true;
    }

    @Transactional
    public void deleteById(final UUID id) {
        this.repository.deleteById(id);
    }

    @Scheduled(fixedDelay = 7, timeUnit = TimeUnit.DAYS)
    @Transactional
    public void cleanExpiredRefreshTokenEntities() {
        this.repository.deleteAllByExpiryDateBefore(Instant.now());
    }
}
