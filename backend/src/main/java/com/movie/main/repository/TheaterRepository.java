package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.dto.TheaterDTO;
import com.movie.main.entity.Theater;
import com.movie.main.repository.base.BaseRepository;

import jakarta.persistence.EntityManager;

@Repository
public class TheaterRepository extends BaseRepository<Theater, TheaterDTO, Integer> {
    protected TheaterRepository(final EntityManager entityManager) {
        super(entityManager, Theater.class, TheaterDTO.class);
    }
}
