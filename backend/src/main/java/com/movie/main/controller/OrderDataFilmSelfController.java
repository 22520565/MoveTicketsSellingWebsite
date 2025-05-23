package com.movie.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.response.OrderDataFilmResponseDto;
import com.movie.main.entity.User;
import com.movie.main.service.CustomerOrderService;
import com.movie.main.service.OrderDataFilmService;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/self/order-data-films")
@PermitAll
public class OrderDataFilmSelfController {
    @NotNull
    private final OrderDataFilmService orderDataFilmService;

    @NotNull
    private final CustomerOrderService customerOrderService;

    protected OrderDataFilmSelfController(@NotNull final OrderDataFilmService orderDataFilmService,
            @NotNull final CustomerOrderService customerOrderService) {
        this.orderDataFilmService = orderDataFilmService;
        this.customerOrderService = customerOrderService;
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderDataFilmResponseDto> findById(@PathVariable final int id,
            @AuthenticationPrincipal final User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final var result = this.orderDataFilmService.findByIdAndCustomerId(id, user.getId());

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(OrderDataFilmController.getResponseDtoFrom(result));
    }
}
