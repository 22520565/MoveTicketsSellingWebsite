package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Customer;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @NotNull
    Optional<Customer> findByIdAndBlockedFalseAndDeletedFalse(final int id);

    @NotNull
    Optional<Customer> findByIdAndBlockedTrueAndDeletedFalse(final int id);

    @NotNull
    Optional<Customer> findByIdAndDeletedFalse(final int id);

    @NotNull
    Optional<Customer> findByIdAndDeletedTrue(final int id);

    @NonNull
    Page<@NotNull Customer> findAllByBlockedFalseAndDeletedFalse(@NonNull final Pageable pageable);

    @NonNull
    Page<@NotNull Customer> findAllByBlockedTrueAndDeletedFalse(@NonNull final Pageable pageable);

    @NonNull
    Page<@NotNull Customer> findAllByDeletedTrue(@NonNull final Pageable pageable);

    @NotNull
    Optional<Customer> findByUsernameAndDeletedFalse(@Nullable final String username);

    @NotNull
    Optional<Customer> findByUsername(@Nullable final String username);

    boolean existsByUsernameAndDeletedFalse(@Nullable final String username);

    boolean existsByUsername(@Nullable final String username);
}
