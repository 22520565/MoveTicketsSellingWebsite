package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.FilmShow;

import jakarta.validation.constraints.NotNull;

@Repository
public interface FilmShowRepository extends JpaRepository<FilmShow, Integer> {
    @NotNull
    Optional<FilmShow> findByIdAndDeletedFalse(final int id);

    @NotNull
    Optional<FilmShow> findByIdAndDeletedTrue(final int id);

    @NonNull
    Page<@NotNull FilmShow> findAllByDeletedFalse(@NonNull final Pageable pageable);

    @NonNull
    Page<@NotNull FilmShow> findAllByDeletedTrue(@NonNull final Pageable pageable);
}
