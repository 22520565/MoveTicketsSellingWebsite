package com.movie.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Theater;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Integer> {}
