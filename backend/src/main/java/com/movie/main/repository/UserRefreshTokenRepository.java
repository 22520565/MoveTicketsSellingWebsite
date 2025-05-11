package com.movie.main.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.UserRefreshToken;
import com.movie.main.entity.User;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, UUID> {
    Optional<UserRefreshToken> findByUser(User user);

    void deleteByUser(User user);

    void deleteAllByExpiryDateBefore(Instant cutoffTime);
}
