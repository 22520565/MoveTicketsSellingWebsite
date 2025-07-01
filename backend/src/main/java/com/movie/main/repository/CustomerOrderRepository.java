package com.movie.main.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.movie.main.dto.internal.RevenueByMonth;
import com.movie.main.dto.response.ItemRevenueResponseDto;
import com.movie.main.dto.response.TicketCategoryRevenueResponseDto;
import com.movie.main.dto.response.TicketRateOfFilmResponseDto;
import com.movie.main.dto.response.TicketServeRateResponseDto;
import com.movie.main.entity.CustomerOrder;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> {
    @Query("""
            SELECT co
            FROM CustomerOrder co
            WHERE (co.customer.id = :customerId)
            """)
    @NotNull
    Page<@NotNull CustomerOrder> findAllByCustomerId(
            @Param("customerId") final int customerId,
            @Nonnull final Pageable pageable);

    @Query("SELECT SUM(co.totalPrice) FROM CustomerOrder co WHERE co.date = :date")
    Long getTotalNetRevenueByDate(@Param("date") final LocalDate date);

    @Query("""
            SELECT SUM(co.totalPriceAfterDiscount)
            FROM CustomerOrder co
                LEFT JOIN OrderDecoratorsOfflineService odos ON (co.id = odos.id)
            WHERE (co.date = :date)
                AND ((odos.invalidReasonPrinted IS NULL) OR (TRIM(odos.invalidReasonPrinted) = ''))
                AND ((odos.invalidReasonServed IS NULL) OR (TRIM(odos.invalidReasonServed) = ''))
            """)
    Long getTotalEffectiveRevenueByDate(@Param("date") final LocalDate date);

    @Query("""
            SELECT SUM(co.totalPrice)
            FROM CustomerOrder co
                INNER JOIN OrderDataFilm odf ON (co.id = odf.id)
                INNER JOIN odf.filmShow.room.theater t
            WHERE (co.date = :date)
                AND (t.id = :theaterId)
            """)
    Long getTotalNetRevenueByDateAndTheaterId(
            @Param("date") final LocalDate date,
            @Param("theaterId") final int theaterId);

    @Query("""
            SELECT SUM(co.totalPriceAfterDiscount)
            FROM CustomerOrder co
                LEFT JOIN OrderDecoratorsOfflineService odos ON (co.id = odos.id)
                INNER JOIN OrderDataFilm odf ON (co.id = odf.id)
                INNER JOIN odf.filmShow.room.theater t
            WHERE (co.date = :date)
                AND (t.id = :theaterId)
                AND ((odos.invalidReasonPrinted IS NULL) OR (TRIM(odos.invalidReasonPrinted) = ''))
                AND ((odos.invalidReasonServed IS NULL) OR (TRIM(odos.invalidReasonServed) = ''))
            """)
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
            GROUP BY MONTH(co.date)
            ORDER BY MONTH(co.date) ASC
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
                AND ((odos.invalidReasonPrinted IS NULL) OR (TRIM(odos.invalidReasonPrinted) = ''))
                AND ((odos.invalidReasonServed IS NULL) OR (TRIM(odos.invalidReasonServed) = ''))
            GROUP BY MONTH(co.date)
            ORDER BY MONTH(co.date)
            """)
    List<RevenueByMonth> getMonthlyEffectiveRevenueByYear(@Param("year") final int year);

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
                AND ((odos.invalidReasonPrinted IS NULL) OR (TRIM(odos.invalidReasonPrinted) = ''))
                AND ((odos.invalidReasonServed IS NULL) OR (TRIM(odos.invalidReasonServed) = ''))
            GROUP BY MONTH(co.date)
            ORDER BY MONTH(co.date)
            """)
    List<RevenueByMonth> getMonthlyEffectiveRevenueByYearAndTheaterId(
            @Param("year") final int year,
            @Param("theaterId") final int theaterId);

    @Query("""
            SELECT new com.movie.main.dto.response.TicketServeRateResponseDto(
                COALESCE(SUM(ot.quantity), 0),
                COALESCE(SUM(CASE
                    WHEN
                        ((odos.invalidReasonPrinted IS NULL) OR (TRIM(odos.invalidReasonPrinted) = ''))
                        AND ((odos.invalidReasonServed IS NULL) OR (TRIM(odos.invalidReasonServed) = ''))
                    THEN 1
                    ELSE 0
                END), 0)
            )
            FROM CustomerOrder co
                LEFT JOIN OrderDataFilm odf ON (co.id = odf.id)
                LEFT JOIN OrderDecoratorsOfflineService odos ON (co.id = odos.id)
                JOIN odf.orderTickets ot
            WHERE co.date = :date
            """)
    TicketServeRateResponseDto getTicketServeRateByDate(@Param("date") final LocalDate date);

    @Query("""
            SELECT new com.movie.main.dto.response.TicketServeRateResponseDto(
                COALESCE(SUM(ot.quantity), 0),
                COALESCE(SUM(CASE
                    WHEN
                        ((odos.invalidReasonPrinted IS NULL) OR (TRIM(odos.invalidReasonPrinted) = ''))
                        AND ((odos.invalidReasonServed IS NULL) OR (TRIM(odos.invalidReasonServed) = ''))
                    THEN 1
                    ELSE 0
                END), 0)
            )
            FROM CustomerOrder co
                LEFT JOIN OrderDataFilm odf ON (co.id = odf.id)
                LEFT JOIN OrderDecoratorsOfflineService odos ON (co.id = odos.id)
                JOIN odf.orderTickets ot
            WHERE co.date = :date
                AND (odf.filmShow.room.theater.id = :theaterId)
            """)
    TicketServeRateResponseDto getTicketServeRateByDateAndTheaterId(
            @Param("date") final LocalDate date,
            @Param("theaterId") final int theaterId);

    @Query("""
            SELECT new com.movie.main.dto.response.TicketCategoryRevenueResponseDto(
                ot.name,
                SUM(ot.quantity * ot.price)
            )
            FROM CustomerOrder co
                INNER JOIN OrderDataFilm odf ON (co.id = odf.id)
                JOIN odf.orderTickets ot
            WHERE co.date = :date
            GROUP BY ot.id
            """)
    @NonNull
    Page<TicketCategoryRevenueResponseDto> getTicketCategoryRevenueByDate(
            @Param("date") final LocalDate date,
            @NonNull final Pageable pageable);

    @Query("""
            SELECT new com.movie.main.dto.response.TicketCategoryRevenueResponseDto(
                ot.name,
                SUM(ot.quantity * ot.price)
            )
            FROM CustomerOrder co
                INNER JOIN OrderDataFilm odf ON (co.id = odf.id)
                JOIN odf.orderTickets ot
            WHERE co.date = :date
                AND (odf.filmShow.room.theater.id = :theaterId)
            GROUP BY ot.id
            """)
    @NonNull
    Page<TicketCategoryRevenueResponseDto> getTicketCategoryRevenueByDateAndTheaterId(
            @Param("date") final LocalDate date,
            @Param("theaterId") final int theaterId,
            @NonNull final Pageable pageable);

    @Query("""
            SELECT new com.movie.main.dto.response.ItemRevenueResponseDto(
                oi.name,
                SUM(oi.quantity * oi.price)
            )
            FROM CustomerOrder co
                INNER JOIN OrderDataItem odi ON (odi.id = co.id)
                JOIN odi.orderItems oi
            WHERE co.date = :date
            GROUP BY oi.id
            """)
    Page<ItemRevenueResponseDto> getAdditionalItemsRevenueByDate(
            @Param("date") final LocalDate date,
            @NonNull final Pageable pageable);

    @Query("""
            SELECT new com.movie.main.dto.response.ItemRevenueResponseDto(
                oi.name,
                SUM(oi.quantity * oi.price)
            )
            FROM CustomerOrder co
                INNER JOIN OrderDataItem odi ON (co.id = odi.id)
                JOIN odi.orderItems oi
                INNER JOIN OrderDataFilm odf ON (co.id = odf.id)
            WHERE co.date = :date
                AND (odf.filmShow.room.theater.id = :theaterId)
            GROUP BY oi.id
            """)
    Page<ItemRevenueResponseDto> getAdditionalItemsRevenueByDateAndTheaterId(
            @Param("date") final LocalDate date,
            @Param("theaterId") final int theaterId,
            @NonNull final Pageable pageable);

    @Query("""
            SELECT new com.movie.main.dto.response.TicketRateOfFilmResponseDto(
                f.name,
                SUM(ot.quantity)
            )
            FROM CustomerOrder co
                INNER JOIN OrderDataFilm odf ON (co.id = odf.id)
                JOIN odf.filmShow.film f
                JOIN odf.orderTickets ot
            WHERE co.date = :date
            GROUP BY f.id
            """)
    Page<TicketRateOfFilmResponseDto> getTicketRateOfFilmByDate(
            @Param("date") final LocalDate date,
            @NonNull final Pageable pageable);

    @Query("""
            SELECT new com.movie.main.dto.response.TicketRateOfFilmResponseDto(
                f.name,
                SUM(ot.quantity)
            )
            FROM CustomerOrder co
                INNER JOIN OrderDataFilm odf ON (co.id = odf.id)
                JOIN odf.filmShow.film f
                JOIN odf.orderTickets ot
            WHERE co.date = :date
                AND (odf.filmShow.room.theater.id = :theaterId)
            GROUP BY f.id
            """)
    Page<TicketRateOfFilmResponseDto> getTicketRateOfFilmByDateAndTheaterId(
            @Param("date") final LocalDate date,
            @Param("theaterId") final int theaterId,
            @NonNull final Pageable pageable);
}
