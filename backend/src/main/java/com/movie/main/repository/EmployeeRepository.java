package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Employee;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    @NotNull
    Optional<Employee> findByIdAndDeletedFalse(final int id);

    @NonNull
    Page<@NotNull Employee> findAll(@NonNull final Pageable pageable);

    @NonNull
    Page<@NotNull Employee> findAllByDeletedFalse(@NonNull final Pageable pageable);

    @NotNull
    Optional<Employee> findByUsernameAndDeletedFalse(@Nullable final String username);

    @NotNull
    Optional<Employee> findByUsername(@Nullable final String username);

    boolean existsByUsernameAndDeletedFalse(@Nullable final String username);

    boolean existsByUsername(@Nullable final String username);
}
