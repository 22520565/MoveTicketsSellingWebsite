package com.movie.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.RoomSeat;

@Repository
public interface RoomSeatRepository extends JpaRepository<RoomSeat, Integer> {
    @Query("""
            SELECT rs
            FROM RoomSeat rs
            WHERE rs.room.id = :roomId
            """)
    List<RoomSeat> getListRoomSeatsByRoomId(@Param("roomId") final int roomId);
}
