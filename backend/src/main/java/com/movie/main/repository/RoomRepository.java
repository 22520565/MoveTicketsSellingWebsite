package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.dto.RoomDTO;
import com.movie.main.entity.Room;

import jakarta.persistence.EntityManager;

@Repository
public class RoomRepository extends AbstractRepository<Room, RoomDTO, Integer> {
    protected RoomRepository(final EntityManager entityManager) {
        super(entityManager, Room.class, RoomDTO.class);
    }

}
