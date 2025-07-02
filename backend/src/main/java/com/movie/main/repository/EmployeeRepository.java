package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Employee;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    @NotNull
    Optional<Employee> findByIdAndBlockedFalseAndDeletedFalse(final int id);

    @NotNull
    Optional<Employee> findByIdAndBlockedTrueAndDeletedFalse(final int id);

    @NotNull
    Optional<Employee> findByIdAndDeletedFalse(final int id);

    @NotNull
    Optional<Employee> findByIdAndDeletedTrue(final int id);

    @NonNull
    Page<@NotNull Employee> findAllByBlockedFalseAndDeletedFalse(@NonNull final Pageable pageable);

    @NonNull
    Page<@NotNull Employee> findAllByBlockedTrueAndDeletedFalse(@NonNull final Pageable pageable);

    @NonNull
    Page<@NotNull Employee> findAllByDeletedTrue(@NonNull final Pageable pageable);

    @NotNull
    Optional<Employee> findByUsernameAndDeletedFalse(@Nullable final String username);

    @NotNull
    Optional<Employee> findByUsername(@Nullable final String username);

    boolean existsByUsernameAndDeletedFalse(@Nullable final String username);

    boolean existsByUsername(@Nullable final String username);

    @Query("SELECT COUNT(e) > 0 FROM Employee e JOIN e.permissions p WHERE p = com.movie.main.entity.Employee.Permission.ADMIN AND NOT e.blocked AND NOT e.deleted")
    boolean existsAnyAdmin();
}
