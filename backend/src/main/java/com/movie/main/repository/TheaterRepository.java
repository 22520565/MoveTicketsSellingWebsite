package com.movie.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Theater;

import jakarta.validation.constraints.NotNull;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Integer> {
    @Query("SELECT DISTINCT f.room.theater FROM FilmShow f " + "WHERE (f.film.id = :filmId) "
            + "AND ((f.showDate > CURRENT_DATE) OR ((f.showDate = CURRENT_DATE) AND (f.showTime >= CURRENT_TIME)))"
            + "ORDER BY f.showDate ASC, f.showTime ASC")
    @NonNull
    Page<@NotNull Theater> findAllByFilmIdAndDeletedFalseOrderByShowDateTimeFromNow(@Param("filmId") final int filmId,
            @NonNull final Pageable pageable);
}
