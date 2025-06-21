package com.movie.main.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.movie.main.dto.internal.RevenueByMonth;
import com.movie.main.dto.response.DailyStatisticResponseDto;
import com.movie.main.dto.response.MonthlyStatisticResponseDto;
import com.movie.main.repository.CustomerOrderRepository;
import com.movie.main.repository.OrderDataFilmRepository;
import com.movie.main.repository.OrderDataItemRepository;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@Service
public class StatisticService {
    @NotNull
    private final CustomerOrderRepository customerOrderRepository;

    @NotNull
    private final OrderDataFilmRepository orderDataFilmRepository;

    @NotNull
    private final OrderDataItemRepository orderDataItemRepository;

    public StatisticService(
            @NotNull final CustomerOrderRepository customerOrderRepository,
            @NotNull final OrderDataFilmRepository orderDataFilmRepository,
            @NotNull final OrderDataItemRepository orderDataItemRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.orderDataFilmRepository = orderDataFilmRepository;
        this.orderDataItemRepository = orderDataItemRepository;
    }

    @NotNull
    public DailyStatisticResponseDto getDailyStatisticByDate(final LocalDate date) {
        final long totalNetRevenue = Objects.requireNonNullElse(
                this.customerOrderRepository.getTotalNetRevenueByDate(date), 0L);

        final long totalEffectiveRevenue = Objects.requireNonNullElse(
                this.customerOrderRepository.getTotalEffectiveRevenueByDate(date), 0L);

        final long totalTicketRevenue = Objects.requireNonNullElse(
                this.orderDataFilmRepository.getTotalTicketRevenueByDate(date), 0L);

        final long totalItemRevenue = Objects.requireNonNullElse(
                this.orderDataItemRepository.getTotalItemRevenueByDate(date), 0L);

        return new DailyStatisticResponseDto(
                totalNetRevenue,
                totalEffectiveRevenue,
                totalTicketRevenue,
                totalItemRevenue);
    }

    @NotNull
    public DailyStatisticResponseDto getDailyStatisticByDateAndTheaterId(
            final LocalDate date,
            final int theaterId) {
        final long totalNetRevenue = Objects.requireNonNullElse(
                this.customerOrderRepository.getTotalNetRevenueByDateAndTheaterId(date, theaterId), 0L);

        final var totalEffectiveRevenue = Objects.requireNonNullElse(
                this.customerOrderRepository.getTotalEffectiveRevenueByDateAndTheaterId(date, theaterId), 0L);

        final var totalTicketRevenue = Objects.requireNonNullElse(
                this.orderDataFilmRepository.getTotalTicketRevenueByDateAndTheaterId(date, theaterId), 0L);

        final var totalItemRevenue = Objects.requireNonNullElse(
                this.orderDataItemRepository.getTotalItemRevenueByDateAndTheaterId(date, theaterId), 0L);

        return new DailyStatisticResponseDto(
                totalNetRevenue,
                totalEffectiveRevenue,
                totalTicketRevenue,
                totalItemRevenue);
    }

    public List<MonthlyStatisticResponseDto> getMonthlyStatisticByYear(final int year) {
        final var monthlyNetRevenue = this.customerOrderRepository
                .getMonthlyNetRevenueByYear(year);

        final var monthlyEffectiveRevenue = this.customerOrderRepository
                .getMonthlyEffectiveRevenueByYear(year);

        final var effectiveMap = monthlyEffectiveRevenue.stream()
                .collect(Collectors.toMap(
                        RevenueByMonth::month,
                        RevenueByMonth::totalRevenue));

        return monthlyNetRevenue.stream()
                .map((final var net) -> {
                    final var effective = effectiveMap.getOrDefault(net.month(), 0L);
                    return new MonthlyStatisticResponseDto(
                            net.month(),
                            net.totalRevenue(),
                            effective);
                })
                .toList();
    }

    public List<MonthlyStatisticResponseDto> getMonthlyStatisticByYearAndTheaterId(
            final int year,
            final int theaterId) {
        final var monthlyNetRevenue = this.customerOrderRepository
                .getMonthlyNetRevenueByYearAndTheaterId(year, theaterId);
        final var monthlyEffectiveRevenue = this.customerOrderRepository
                .getMonthlyEffectiveRevenueByYearAndTheaterId(year, theaterId);

        final var effectiveMap = monthlyEffectiveRevenue.stream()
                .collect(Collectors.toMap(
                        RevenueByMonth::month,
                        RevenueByMonth::totalRevenue));

        return monthlyNetRevenue.stream()
                .map((final var net) -> {
                    final var effective = effectiveMap.getOrDefault(net.month(), 0L);
                    return new MonthlyStatisticResponseDto(
                            net.month(),
                            net.totalRevenue(),
                            effective);
                })
                .toList();
    }
}
