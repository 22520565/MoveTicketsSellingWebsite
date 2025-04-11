package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.dto.RoomSeatDto;
import com.movie.main.entity.RoomSeat;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;

@Repository
public class RoomSeatRepository extends AbstractRepository<RoomSeat, RoomSeatDto, Integer> {
    protected RoomSeatRepository(@NotNull final EntityManager entityManager) {
        super(entityManager, RoomSeat.class, RoomSeatDto.class);
    }

}
