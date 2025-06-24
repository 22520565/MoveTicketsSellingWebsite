package com.movie.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.CreateOrderRequestDto;
import com.movie.main.dto.response.CustomerOrderResponseDto;
import com.movie.main.entity.User;
import com.movie.main.service.MainOrderService;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/main-order")
@PermitAll
public class MainOrderController {
    @NotNull
    private final MainOrderService service;

    public MainOrderController(@NotNull final MainOrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CustomerOrderResponseDto> createOrder(
            @Valid final CreateOrderRequestDto requestDto,
            @AuthenticationPrincipal final User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final var result = this.service.createOrder(requestDto, user.getId());
        final var customerOrder = result.getValue();
        if (customerOrder != null) {
            return ResponseEntity.ok(new CustomerOrderResponseDto(
                    customerOrder.getId(),
                    customerOrder.getDate(),
                    customerOrder.getVerifyCode(),
                    customerOrder.getTotalPrice(),
                    customerOrder.getTotalPriceAfterDiscount(),
                    customerOrder.getCustomer().getId()));
        }

        return ResponseEntity.internalServerError().build();
    }
}
