package com.movie.main.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movie.main.dto.internal.RevenueByMonth;
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
            + "WHERE (co.date = :date) AND (t.id = :theaterId)")
    Long getTotalNetRevenueByDateAndTheaterId(
            @Param("date") final LocalDate date,
            @Param("theaterId") final int theaterId);

    @Query("SELECT SUM(co.totalPriceAfterDiscount) FROM CustomerOrder co "
            + "LEFT JOIN OrderDecoratorsOfflineService odos ON (co.id = odos.id)"
            + "INNER JOIN OrderDataFilm odf ON (co.id = odf.id) "
            + "INNER JOIN odf.filmShow.room.theater t "
            + "WHERE (co.date = :date) AND (t.id = :theaterId) "
            + "AND (odos.invalidReasonPrinted IS NULL) AND (odos.invalidReasonServed IS NULL)")
    Long getTotalEffectiveRevenueByDateAndTheaterId(
            @Param("date") final LocalDate date,
            @Param("theaterId") final int theaterId);

    @Query("""
            SELECT new com.movie.main.dto.internal.RevenueByMonth(
                MONTH(co.date),
                SUM(co.totalPrice)
            )
            FROM CustomerOrder co
            WHERE YEAR(co.date) = :year
            GROUP BY MONTH(co.date) ORDER BY MONTH(co.date) ASC
            """)
    List<RevenueByMonth> getMonthlyNetRevenueByYear(@Param("year") final int year);

    @Query("""
            SELECT new com.movie.main.dto.internal.RevenueByMonth(
                MONTH(co.date),
                SUM(co.totalPrice)
            )
            FROM CustomerOrder co
            INNER JOIN OrderDataFilm odf ON (co.id = odf.id)
            INNER JOIN odf.filmShow.room.theater t
            WHERE (YEAR(co.date) = :year)
                AND (t.id = :theaterId)
            GROUP BY MONTH(co.date)
            ORDER BY MONTH(co.date) ASC
            """)
    List<RevenueByMonth> getMonthlyNetRevenueByYearAndTheaterId(
            @Param("year") final int year,
            @Param("theaterId") final int theaterId);

    @Query("""
            SELECT new com.movie.main.dto.internal.RevenueByMonth(
                MONTH(co.date),
                SUM(co.totalPriceAfterDiscount)
            )
            FROM CustomerOrder co
            LEFT JOIN OrderDecoratorsOfflineService odos ON (co.id = odos.id)
            WHERE (YEAR(co.date) = :year)
                AND (odos.invalidReasonPrinted IS NULL)
                AND (odos.invalidReasonServed IS NULL)
            GROUP BY MONTH(co.date)
            ORDER BY MONTH(co.date)
            """)
    List<RevenueByMonth> getMonthlyEffectiveRevenueByYear(@Param("year") int year);

    @Query("""
            SELECT new com.movie.main.dto.internal.RevenueByMonth(
                MONTH(co.date),
                SUM(co.totalPriceAfterDiscount)
            )
            FROM CustomerOrder co
            LEFT JOIN OrderDecoratorsOfflineService odos ON (co.id = odos.id)
            INNER JOIN OrderDataFilm odf ON (co.id = odf.id)
            INNER JOIN odf.filmShow.room.theater t
            WHERE (YEAR(co.date) = :year)
                AND (t.id = :theaterId)
                AND (odos.invalidReasonPrinted IS NULL)
                AND (odos.invalidReasonServed IS NULL)
            GROUP BY MONTH(co.date)
            ORDER BY MONTH(co.date)
            """)
    List<RevenueByMonth> getMonthlyEffectiveRevenueByYearAndTheaterId(
            @Param("year") int year,
            @Param("theaterId") final int theaterId);
}
