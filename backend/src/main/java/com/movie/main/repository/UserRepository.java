package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.User;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @NotNull
    Optional<User> findByUsernameAndDeletedFalse(@Nullable final String username);

    @NotNull
    Optional<User> findByUsername(@Nullable final String username);

    boolean existsByUsernameAndDeletedFalse(@Nullable final String username);

    boolean existsByUsername(@Nullable final String username);
}
