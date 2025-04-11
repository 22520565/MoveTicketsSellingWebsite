package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.entity.Film;

@Repository
public interface FilmRepository extends InterfaceRepository<Film, Integer> {
}
