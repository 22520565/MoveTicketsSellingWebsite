package com.movie.main.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.RoomSeat;

import jakarta.annotation.Nonnull;

@Repository
public interface RoomSeatRepository extends JpaRepository<RoomSeat, Integer> {
    @Query("""
            SELECT rs
            FROM RoomSeat rs
            WHERE (rs.room.id = :roomId)
            """)
    List<RoomSeat> getListRoomSeatsByRoomId(@Param("roomId") final int roomId);

    @Query("""
            SELECT rs
            FROM RoomSeat rs
                JOIN rs.room r
                INNER JOIN FilmShow fs ON (r.id = fs.room.id)
            WHERE (fs.id = :filmShowId)
            """)
    Page<RoomSeat> findAllByFilmShowId(
            @Param("filmShowId") final int filmShowId,
            @Nonnull final Pageable pageable);

    @Query("""
            SELECT DISTINCT rs
            FROM RoomSeat rs
            JOIN rs.room r
            JOIN FilmShow fs ON fs.room.id = r.id
            WHERE fs.id = :filmShowId
              AND (
                rs IN (
                    SELECT rs1 FROM OrderDataFilm odf JOIN odf.roomSeats rs1
                    WHERE odf.filmShow.id = :filmShowId
                )
                OR rs IN (
                    SELECT rsl.roomSeat FROM RoomSeatLock rsl
                    WHERE rsl.filmShow.id = :filmShowId
                      AND rsl.expireAt > CURRENT_TIMESTAMP
                )
              )
            """)
    Page<RoomSeat> findAllUnusableByFilmShowId(
            @Param("filmShowId") final int filmShowId,
            @Nonnull final Pageable pageable);
}
