package com.movie.main.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.movie.main.entity.User;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@Repository
public interface UserRepository extends InterfaceRepository<User, Integer> {
    @NotNull
    Optional<User> findByUsername(@Nullable final String username);

    boolean existsByUsername(@Nullable final String username);
}
