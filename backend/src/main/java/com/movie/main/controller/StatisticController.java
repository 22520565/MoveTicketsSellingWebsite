package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.auth.RequirePermission;
import com.movie.main.config.OpenApiConfig;
import com.movie.main.dto.response.DailyStatisticResponseDto;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.service.StatisticService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("daily")
    public ResponseEntity<DailyStatisticResponseDto> getDailyStatistic(
            @RequestParam @NotNull final LocalDate date) {
        return ResponseEntity.ok(this.service.getDailyStatistic(date));
    }
}
