package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Film;

import jakarta.validation.constraints.NotNull;

@Repository
public interface FilmRepository extends JpaRepository<Film, Integer> {
    @NotNull
    Optional<Film> findByIdAndDeletedFalse(final int id);

    @NotNull
    Optional<Film> findByIdAndDeletedTrue(final int id);

    @NonNull
    Page<@NotNull Film> findAllByDeletedFalse(@NonNull final Pageable pageable);

    @NonNull
    Page<@NotNull Film> findAllByDeletedTrue(@NonNull final Pageable pageable);

    @Query("SELECT DISTINCT f FROM Film f JOIN f.tags t " + "WHERE f.deleted = false AND ("
            + "LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(f.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))" + ")")
    @NonNull
    Page<@NotNull Film> searchAllFilmsWithTagsByDeletedFalse(@Param("keyword") String keyword,
            @NonNull Pageable pageable);
}
