package com.movie.main.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.movie.main.dto.response.DailyStatisticResponseDto;
import com.movie.main.repository.CustomerOrderRepository;

import jakarta.validation.constraints.NotNull;

@Service
public class StatisticService {
    @NotNull
    private final CustomerOrderRepository customerOrderRepository;

    public StatisticService(@NotNull final CustomerOrderRepository customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }

    public DailyStatisticResponseDto getDailyStatistic(final LocalDate date) {
        return new DailyStatisticResponseDto(
                this.customerOrderRepository.getTotalNetRevenueByDate(date),
                this.customerOrderRepository.getTotalEffectiveRevenueByDate(date));
    }
}
