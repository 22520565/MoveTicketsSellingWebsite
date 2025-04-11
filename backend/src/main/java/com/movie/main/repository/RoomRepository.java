package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.dto.RoomDto;
import com.movie.main.entity.Room;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;

@Repository
public class RoomRepository extends AbstractRepository<Room, RoomDto, Integer> {
    protected RoomRepository(@NotNull final EntityManager entityManager) {
        super(entityManager, Room.class, RoomDto.class);
    }

}
