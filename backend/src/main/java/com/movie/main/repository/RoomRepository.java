package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.entity.Room;

@Repository
public interface RoomRepository extends InterfaceRepository<Room, Integer> {
}
