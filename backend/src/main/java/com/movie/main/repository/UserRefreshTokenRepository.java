package com.movie.main.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.movie.main.entity.UserRefreshToken;
import com.movie.main.entity.User;

@Repository
public interface UserRefreshTokenRepository extends InterfaceRepository<UserRefreshToken, Integer> {
    boolean existsByRefreshToken(final UUID refreshToken);

    Optional<UserRefreshToken> findByRefreshToken(UUID refreshToken);

    Optional<UserRefreshToken> findByUser(User user);

    void deleteByRefreshToken(UUID refreshToken);

    void deleteByUser(User user);

    void deleteAllByExpiryDateBefore(Instant cutoffTime);
}
