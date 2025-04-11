package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.dto.TheaterDto;
import com.movie.main.entity.Theater;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;

@Repository
public class TheaterRepository extends AbstractRepository<Theater, TheaterDto, Integer> {
    protected TheaterRepository(@NotNull final EntityManager entityManager) {
        super(entityManager, Theater.class, TheaterDto.class);
    }
}
