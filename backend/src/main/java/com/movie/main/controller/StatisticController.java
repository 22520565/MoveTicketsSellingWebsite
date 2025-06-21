package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.auth.RequirePermission;
import com.movie.main.config.OpenApiConfig;
import com.movie.main.dto.response.DailyStatisticResponseDto;
import com.movie.main.dto.response.MonthlyStatisticResponseDto;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.service.StatisticService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/statistics")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class StatisticController {
    @NotNull
    private final StatisticService service;

    public StatisticController(@NotNull final StatisticService service) {
        this.service = service;
    }

    @GetMapping("daily/{date}")
    public ResponseEntity<DailyStatisticResponseDto> getDailyStatisticByDate(
            @PathVariable @NotNull final LocalDate date) {
        final var dailyStatistic = this.service.getDailyStatisticByDate(date);
        return ResponseEntity.ok(dailyStatistic);
    }

    @GetMapping("daily/{date}/theater/{theaterId}")
    public ResponseEntity<DailyStatisticResponseDto> getDailyStatisticByDateAndTheaterId(
            @PathVariable @NotNull final LocalDate date,
            @PathVariable final int theaterId) {
        final var dailyStatistic = this.service.getDailyStatisticByDateAndTheaterId(date, theaterId);
        return ResponseEntity.ok(dailyStatistic);
    }

    @GetMapping("monthly/{year}")
    public ResponseEntity<List<MonthlyStatisticResponseDto>> getMonthlyStatisticByYear(
            @PathVariable final int year) {
        final var monthlyStatistic = this.service.getMonthlyStatisticByYear(year);
        return ResponseEntity.ok(monthlyStatistic);
    }

    @GetMapping("monthly/{year}/theater/{theaterId}")
    public ResponseEntity<List<MonthlyStatisticResponseDto>> getMonthlyStatisticByYearAndTheaterId(
            @PathVariable final int year,
            @PathVariable final int theaterId) {
        final var monthlyStatistic = this.service.getMonthlyStatisticByYearAndTheaterId(year, theaterId);
        return ResponseEntity.ok(monthlyStatistic);
    }
}
