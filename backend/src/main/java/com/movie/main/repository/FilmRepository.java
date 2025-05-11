package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Film;

import jakarta.validation.constraints.NotNull;

@Repository
public interface FilmRepository extends JpaRepository<Film, Integer> {
    @NotNull
    Optional<Film> findByIdAndDeletedFalse(final int id);

    @NonNull
    Page<@NotNull Film> findAll(@NonNull final Pageable pageable);

    @NonNull
    Page<@NotNull Film> findAllByDeletedFalse(@NonNull final Pageable pageable);
}
