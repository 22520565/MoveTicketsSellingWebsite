package com.movie.main.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
