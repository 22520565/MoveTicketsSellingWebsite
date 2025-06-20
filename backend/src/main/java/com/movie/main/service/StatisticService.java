package com.movie.main.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.movie.main.dto.response.DailyStatisticResponseDto;
import com.movie.main.repository.CustomerOrderRepository;
import com.movie.main.repository.OrderDataFilmRepository;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@Service
public class StatisticService {
    @NotNull
    private final CustomerOrderRepository customerOrderRepository;

    @NotNull
    private final OrderDataFilmRepository orderDataFilmRepository;

    public StatisticService(
            @NotNull final CustomerOrderRepository customerOrderRepository,
            @NotNull final OrderDataFilmRepository orderDataFilmRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.orderDataFilmRepository = orderDataFilmRepository;
    }

    @Nullable
    public DailyStatisticResponseDto getDailyStatisticByDate(final LocalDate date) {
        final var totalNetRevenue = this.customerOrderRepository.getTotalNetRevenueByDate(date);
        if (totalNetRevenue == null) {
            return null;
        }

        final var totalEffectiveRevenue = this.customerOrderRepository.getTotalEffectiveRevenueByDate(date);
        if (totalEffectiveRevenue == null) {
            return null;
        }

        final var totalTicketRevenue = this.orderDataFilmRepository.getTotalTicketRevenueByDate(date);
        if (totalTicketRevenue == null) {
            return null;
        }

        return new DailyStatisticResponseDto(
                totalNetRevenue,
                totalEffectiveRevenue,
                totalTicketRevenue);
    }

    @Nullable
    public DailyStatisticResponseDto getDailyStatisticByDateAndTheaterId(
            final LocalDate date, final int theaterId) {
        final var totalNetRevenue = this.customerOrderRepository
                .getTotalNetRevenueByDateAndTheaterId(date, theaterId);
        if (totalNetRevenue == null) {
            return null;
        }

        final var totalEffectiveRevenue = this.customerOrderRepository
                .getTotalEffectiveRevenueByDateAndTheaterId(date, theaterId);
        if (totalEffectiveRevenue == null) {
            return null;
        }

        final var totalTicketRevenue = this.orderDataFilmRepository
                .getTotalTicketRevenueByDateAndTheaterId(date, theaterId);
        if (totalTicketRevenue == null) {
            return null;
        }

        return new DailyStatisticResponseDto(
                totalNetRevenue,
                totalEffectiveRevenue,
                totalTicketRevenue);
    }
}
