package com.movie.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.movie.main.entity.Film;
import com.movie.main.entity.FilmTag;
import com.movie.main.entity.FilmTagId;

public interface FilmTagRepository extends InterfaceRepository<FilmTag, FilmTagId> {
    @Query("SELECT ft.film.id FROM FilmTag ft WHERE ft.tag.id = :tagId")
    Page<Integer> findFilmIdsByTagId(@Param("tagId") Integer tagId, Pageable pageable);

    @Query("SELECT ft.film FROM FilmTag ft WHERE ft.tag.id = :tagId")
    Page<Film> findFilmsByTagId(@Param("tagId") Integer tagId, Pageable pageable);

    @Query("SELECT ft.tag.id FROM FilmTag ft WHERE ft.film.id = :filmId")
    Page<Integer> findTagIdsByFilmId(@Param("filmId") Integer filmId, Pageable pageable);

    @Query("SELECT ft.tag FROM FilmTag ft WHERE ft.film.id = :filmId")
    Page<Integer> findTagsByFilmId(@Param("filmId") Integer filmId, Pageable pageable);
}
