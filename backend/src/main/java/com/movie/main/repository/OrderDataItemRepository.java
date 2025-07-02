package com.movie.main.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movie.main.dto.response.BestSellingItemResponseDto;
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

    @Query("""
            SELECT new com.movie.main.dto.response.BestSellingItemResponseDto(
                oi.name,
                SUM(oi.quantity)
            )
            FROM OrderDataItem odi
            JOIN odi.customerOrder co
            JOIN odi.orderItems oi
            WHERE co.date = :date
            GROUP BY oi.id
            ORDER BY SUM(oi.quantity) DESC
            LIMIT 1
            """)
    BestSellingItemResponseDto getBestSellingItemByDay(@Param("date") final LocalDate date);

    @Query("""
            SELECT new com.movie.main.dto.response.BestSellingItemResponseDto(
                oi.name,
                SUM(oi.quantity)
            )
            FROM OrderDataItem odi
                JOIN odi.customerOrder co
                JOIN odi.orderItems oi
                INNER JOIN OrderDataFilm odf ON (co.id = odf.id)
            WHERE co.date = :date
                AND odf.filmShow.room.theater.id = :theaterId
            GROUP BY oi.id
            ORDER BY SUM(oi.quantity) DESC
            LIMIT 1
            """)
    BestSellingItemResponseDto getBestSellingItemByDayAndTheaterId(
            @Param("date") final LocalDate date,
            @Param("theaterId") final int theaterId);
}
