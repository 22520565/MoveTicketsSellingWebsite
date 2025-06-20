package com.movie.main.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.OrderDataItem;

@Repository
public interface OrderDataItemRepository extends JpaRepository<OrderDataItem, Integer> {
    @Query("SELECT SUM(ot.quantity * ot.price) FROM OrderDataItem odi "
            + "INNER JOIN odi.customerOrder co "
            + "INNER JOIN odi.orderItems ot "
            + "WHERE (co.date = :date)")
    Long getTotalItemRevenueByDate(@Param("date") final LocalDate date);

    @Query("SELECT SUM(ot.quantity * ot.price) FROM OrderDataItem odi "
            + "INNER JOIN odi.customerOrder co "
            + "INNER JOIN odi.orderItems ot "
            + "INNER JOIN OrderDataFilm odf ON (co.id = odf.id)"
            + "INNER JOIN odf.filmShow.room.theater t "
            + "WHERE (co.date = :date) AND (t.id = :theaterId)")
    Long getTotalItemRevenueByDateAndTheaterId(
            @Param("date") final LocalDate date,
            @Param("theaterId") final int theaterId);
}
