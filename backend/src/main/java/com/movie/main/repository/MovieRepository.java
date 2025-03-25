package com.movie.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    Movie findByName(final String name);
}
