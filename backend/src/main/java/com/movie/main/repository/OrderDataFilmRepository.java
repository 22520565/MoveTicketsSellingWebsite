package com.movie.main.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movie.main.dto.response.HotFilmResponseDto;
import com.movie.main.entity.OrderDataFilm;

import jakarta.validation.constraints.NotNull;

@Repository
public interface OrderDataFilmRepository extends JpaRepository<OrderDataFilm, Integer> {
    @Query("SELECT o FROM OrderDataFilm o WHERE (o.id = :id) AND (o.customerOrder.customer.id = : customerId)")
    @NotNull
    Optional<OrderDataFilm> findByIdAndCustomerId(@Param("id") final int id, @Param("customerId") final int customerId);

    @Query("SELECT SUM(t.quantity * t.price) FROM OrderDataFilm o INNER JOIN o.orderTickets t "
            + "WHERE (o.date = :date)")
    Long getTotalTicketRevenueByDate(@Param("date") final LocalDate date);

    @Query("SELECT SUM(ot.quantity * ot.price) FROM OrderDataFilm odf "
            + "INNER JOIN odf.orderTickets ot "
            + "INNER JOIN odf.filmShow.room.theater t "
            + "WHERE (odf.date = :date) AND (t.id = :theaterId)")
    Long getTotalTicketRevenueByDateAndTheaterId(
            @Param("date") final LocalDate date,
            @Param("theaterId") final int theaterId);

    @Query("""
            SELECT new com.movie.main.dto.response.HotFilmResponseDto(
                f.name,
                COUNT(rs)
            )
            FROM OrderDataFilm odf
                JOIN odf.customerOrder co
                JOIN odf.filmShow.film f
                JOIN odf.roomSeats rs
            WHERE co.date = :date
            GROUP BY f.id
            ORDER BY COUNT(rs) DESC
            LIMIT 1
            """)
    HotFilmResponseDto getHotFilmOfDay(
            @Param("date") final LocalDate date);

    @Query("""
            SELECT new com.movie.main.dto.response.HotFilmResponseDto(
                f.name,
                COUNT(rs)
            )
            FROM OrderDataFilm odf
                JOIN odf.customerOrder co
                JOIN odf.filmShow.film f
                JOIN odf.roomSeats rs
            WHERE (co.date = :date)
                AND (odf.filmShow.room.theater.id = :theaterId)
            GROUP BY f.id
            ORDER BY COUNT(rs) DESC
            LIMIT 1
            """)
    HotFilmResponseDto getHotFilmOfDayByTheaterId(
            @Param("date") final LocalDate date,
            @Param("theaterId") final int theaterId);

    @Query("""
            SELECT COUNT(rs) > 0
            FROM OrderDataFilm odf
                JOIN odf.roomSeats rs
            WHERE (odf.filmShow.id = :filmShowId)
                AND (rs.id = :roomSeatId)
            """)
    boolean isRoomSeatTakenByFilmShowId(
            @Param("roomSeatId") final int roomSeatId,
            @Param("filmShowId") final int filmShowId);

    @Query("""
            SELECT COUNT(rs) <= 0
            FROM OrderDataFilm odf
                JOIN odf.roomSeats rs
            WHERE (odf.filmShow.id = :filmShowId)
                AND (rs.id = :roomSeatId)
            """)
    boolean isRoomSeatUsableByFilmShowId(
            @Param("roomSeatId") final int roomSeatId,
            @Param("filmShowId") final int filmShowId);
}
