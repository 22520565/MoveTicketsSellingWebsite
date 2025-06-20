package com.movie.main.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.CustomerOrder;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> {
    @Query("SELECT SUM(co.totalPrice) FROM CustomerOrder co WHERE co.date = :date")
    Long getTotalNetRevenueByDate(@Param("date") final LocalDate date);

    @Query("SELECT SUM(co.totalPriceAfterDiscount) FROM CustomerOrder co "
            + "LEFT JOIN OrderDecoratorsOfflineService odos ON (co.id = odos.id)"
            + "WHERE (co.date = :date) AND (odos.invalidReasonPrinted IS NULL) AND (odos.invalidReasonServed IS NULL)")
    Long getTotalEffectiveRevenueByDate(@Param("date") final LocalDate date);

    @Query("SELECT SUM(co.totalPrice) FROM CustomerOrder co "
            + "INNER JOIN OrderDataFilm odf ON (co.id = odf.id) "
            + "INNER JOIN odf.filmShow.room.theater t "
            + "WHERE (co.date = :date) AND (t.id = :theater)")
    Long getTotalNetRevenueByDateAndTheaterId(
            @Param("date") final LocalDate date,
            @Param("theaterId") final int theaterId);
}
