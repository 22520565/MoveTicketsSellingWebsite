package com.movie.main.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.movie.main.dto.response.DailyStatisticResponseDto;
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

        final var totalItemRevenue = this.orderDataItemRepository.getTotalItemRevenueByDate(date);
        if (totalItemRevenue == null) {
            return null;
        }

        return new DailyStatisticResponseDto(
                totalNetRevenue,
                totalEffectiveRevenue,
                totalTicketRevenue,
                totalItemRevenue);
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

        final var totalItemRevenue = this.orderDataItemRepository
                .getTotalItemRevenueByDateAndTheaterId(date, theaterId);
        if (totalItemRevenue == null) {
            return null;
        }

        return new DailyStatisticResponseDto(
                totalNetRevenue,
                totalEffectiveRevenue,
                totalTicketRevenue,
                totalItemRevenue);
    }
}
