package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.auth.RequirePermission;
import com.movie.main.config.OpenApiConfig;
import com.movie.main.dto.response.BestSellingItemResponseDto;
import com.movie.main.dto.response.DailyStatisticResponseDto;
import com.movie.main.dto.response.FilmStatisticsResponseDto;
import com.movie.main.dto.response.HotFilmResponseDto;
import com.movie.main.dto.response.ItemRevenueResponseDto;
import com.movie.main.dto.response.MonthlyStatisticResponseDto;
import com.movie.main.dto.response.TicketCategoryRevenueResponseDto;
import com.movie.main.dto.response.TicketServeRateResponseDto;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.service.StatisticService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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

    @GetMapping("film")
    public ResponseEntity<FilmStatisticsResponseDto> getFilmStatisticByDateAndTheaterId(
            @RequestParam final LocalDate date,
            @RequestParam(required = false) final Integer theaterId,
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size) {
        if (theaterId == null) {
            final var result = this.service.getFilmStatisticByDate(date, PageRequest.of(page, size));
            return ResponseEntity.ok(result);
        }

        final var result = this.service.getFilmStatisticByDateAndTheaterId(date, theaterId, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("hot-film")
    public ResponseEntity<HotFilmResponseDto> getHotMovieByDateAndTheaterId(
            @RequestParam final LocalDate date,
            @RequestParam(required = false) final Integer theaterId) {
        if (theaterId == null) {
            return ResponseEntity.ok(this.service.getHotFilmByDate(date));
        }

        return ResponseEntity.ok(this.service.getHotFilmByDateAndTheaterId(date, theaterId));
    }

    @GetMapping("best-selling-item")
    public ResponseEntity<BestSellingItemResponseDto> getBestSellingItemByDateAndTheaterId(
            @RequestParam final LocalDate date,
            @RequestParam(required = false) final Integer theaterId) {
        if (theaterId == null) {
            return ResponseEntity.ok(this.service.getBestSellingItemByDate(date));
        }

        return ResponseEntity.ok(this.service.getBestSellingItemByDateAndTheaterId(date, theaterId));
    }

    @GetMapping("ticket-serve-rate")
    public ResponseEntity<TicketServeRateResponseDto> getTicketServeRateByDateAndTheaterId(
            @RequestParam final LocalDate date,
            @RequestParam(required = false) final Integer theaterId) {
        if (theaterId == null) {
            return ResponseEntity.ok(this.service.getTicketServeRateByDate(date));
        }

        return ResponseEntity.ok(this.service.getTicketServeRateByDateAndTheaterId(date, theaterId));
    }

    @GetMapping("ticket-category-rate")
    public ResponseEntity<PagedModel<EntityModel<TicketCategoryRevenueResponseDto>>> getTicketCategoryRevenueByDate(
            @RequestParam final LocalDate date,
            @RequestParam(required = false) final Integer theaterId,
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<TicketCategoryRevenueResponseDto> assembler) {
        if (theaterId == null) {
            final var result = this.service.getTicketCategoryRevenueByDate(date, PageRequest.of(page, size));
            return ResponseEntity.ok(assembler.toModel(result));
        }

        final var result = this.service.getTicketCategoryRevenueByDateAndTheaterId(
                date, theaterId, PageRequest.of(page, size));
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("additional-items-rate")
    public ResponseEntity<PagedModel<EntityModel<ItemRevenueResponseDto>>> getAdditionalItemsRevenueByDate(
            @RequestParam final LocalDate date,
            @RequestParam(required = false) final Integer theaterId,
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<ItemRevenueResponseDto> assembler) {
        if (theaterId == null) {
            final var result = this.service.getAdditionalItemsRevenueByDate(date, PageRequest.of(page, size));
            return ResponseEntity.ok(assembler.toModel(result));
        }

        final var result = this.service.getAdditionalItemsRevenueByDateAndTheaterId(
                date, theaterId, PageRequest.of(page, size));
        return ResponseEntity.ok(assembler.toModel(result));
    }
}
