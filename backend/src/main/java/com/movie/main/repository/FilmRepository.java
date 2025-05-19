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

    @Query("SELECT DISTINCT f FROM Film f JOIN f.tags t " + "WHERE (NOT f.deleted) AND ("
            + "LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(f.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))" + ")")
    @NonNull
    Page<@NotNull Film> searchAllFilmsWithTagsByDeletedFalse(@Param("keyword") final String keyword,
            @NonNull final Pageable pageable);

    @Query("SELECT DISTINCT f.film FROM FilmShow f " + "WHERE (NOT f.deleted) AND (NOT f.film.deleted) "
            + "AND (f.room.theater.id = :theaterId) "
            + "AND ((f.showDate > CURRENT_DATE) OR ((f.showDate = CURRENT_DATE) AND (f.showTime >= CURRENT_TIMESTAMP)))"
            + "ORDER BY f.showDate ASC, f.showTime ASC")
    @NonNull
    Page<@NotNull Film> findAllByTheaterIdAndDeletedFalseOrderByShowDate(@Param("theaterId") final int theaterId,
            @NonNull final Pageable pageable);
}
