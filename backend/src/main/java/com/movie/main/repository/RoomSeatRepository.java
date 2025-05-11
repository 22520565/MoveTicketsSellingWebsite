package com.movie.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.RoomSeat;

@Repository
public interface RoomSeatRepository extends JpaRepository<RoomSeat, Integer> {}
