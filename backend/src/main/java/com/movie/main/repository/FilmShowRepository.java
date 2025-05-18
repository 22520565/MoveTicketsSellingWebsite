package com.movie.main.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Film;
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

    @Query("SELECT f FROM FilmShow f WHERE (f.film.id = :filmId) AND (f.deleted = false) ORDER BY f.showDate ASC, f.showTime ASC")
    Page<@NotNull FilmShow> findAllByFilmIdOrderByDateTime(@Param("filmId") int filmId,
            @NonNull final Pageable pageable);

    @Query("SELECT f FROM FilmShow f WHERE (f.showDate = :date) AND (f.deleted = false)")
    @NonNull
    Page<@NotNull FilmShow> findAllByShowDateAndDeletedFalse(@Param("date") @NotNull final LocalDate date,
            @NonNull final Pageable pageable);

    @Query("SELECT f FROM FilmShow f WHERE (f.film.id = :filmId) AND (f.showDate = :date) AND (f.deleted = false)")
    @NonNull
    Page<@NotNull FilmShow> findAllByFilmIdAndShowDateAndDeletedFalse(@Param("filmId") int filmId,
            @Param("date") @NotNull final LocalDate date, @NonNull final Pageable pageable);

    @Query(value = "SELECT DISTINCT f.film FROM FilmShow f WHERE (f.showDate = :date) AND (f.deleted = false)")
    @NonNull
    Page<@NotNull Film> findAllFilmsByShowDateAndDeletedFalse(@Param("date") @NotNull final LocalDate date,
            @NonNull final Pageable pageable);

    @Query(value = "SELECT DISTINCT f.film FROM FilmShow f WHERE (f.showDate = CURRENT_DATE) AND (f.showTime >= CURRENT_TIMESTAMP) AND (f.deleted = false) ORDER BY f.showTime ASC")
    @NonNull
    Page<@NotNull Film> findAllFilmsShowingFromNowToEndOfTodayAndDeletedFalse(@NonNull final Pageable pageable);

    @Query("SELECT DISTINCT f.film FROM FilmShow f WHERE (f.showDate BETWEEN :startDate AND :endDate) AND (f.deleted = false) ORDER BY f.showDate ASC, f.showTime ASC")
    @NonNull
    Page<@NotNull Film> findAllFilmsByShowDateRangeAndDeletedFalse(
            @Param("startDate") @NotNull final LocalDate startDate, @Param("endDate") @NotNull final LocalDate endDate,
            @NonNull final Pageable pageable);
}
