package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.entity.FilmShow;

@Repository
public interface FilmShowRepository extends InterfaceSoftDeletableRepository<FilmShow, Integer> {}
