package com.movie.main.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.movie.main.entity.User;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@Repository
public interface UserRepository extends InterfaceSoftDeletableRepository<User, Integer> {
    @NotNull
    Optional<User> findByUsernameAndDeletedFalse(@Nullable final String username);

    boolean existsByUsernameAndDeletedFalse(@Nullable final String username);
}
