package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;

import com.movie.main.entity.User;
import com.movie.main.entity.UserDetailsInterface;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@NoRepositoryBean
public interface UserDetailsRepository<TUser extends UserDetailsInterface>
        extends InterfaceRepository<TUser, Integer> {
    @NotNull
    Optional<TUser> findByUser(@Nullable final User user);

    @NotNull
    Optional<TUser> findByUserUsername(@Nullable final String username);

    boolean existsByUserUsername(@Nullable final String username);
}
