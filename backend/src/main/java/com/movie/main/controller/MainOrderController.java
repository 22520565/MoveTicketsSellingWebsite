package com.movie.main.controller;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.CreateOrderRequestDto;
import com.movie.main.dto.response.CreateOrderResponseDto;
import com.movie.main.dto.response.StripePaymentCreateIntentResponseDto;
import com.movie.main.entity.User;
import com.movie.main.service.MainOrderService;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/main-order")
@PermitAll
public class MainOrderController {
    @NotNull
    private final MainOrderService service;

    public MainOrderController(
            @NotNull final MainOrderService service) {
        this.service = service;
    }

    @PostMapping("stripe-intent")
    public ResponseEntity<StripePaymentCreateIntentResponseDto> createStripePaymentIntent(
            @RequestBody CreateOrderRequestDto requestDto,
            @AuthenticationPrincipal final User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final var result = this.service.createPaymentIntent(requestDto, user.getId());

        final var clientSecret = result.getValue();
        if (clientSecret != null) {
            return ResponseEntity.ok(new StripePaymentCreateIntentResponseDto(clientSecret));
        }

        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("stripe-webhook")
    public ResponseEntity<CreateOrderResponseDto> handleStripeWebhook(
            @RequestHeader("Stripe-Signature") String sigHeader,
            @RequestBody String payload) {
        final var result = this.service.handleStripeWebhook(sigHeader, payload);

        final var responseDto = result.getValue();
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        }

        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<CreateOrderResponseDto> findOrderById(@PathVariable final int id) {
        final var responseDto = this.service.getOrder(id);
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        }

        return ResponseEntity.internalServerError().build();
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponseDto> createOrder(
            @Valid final CreateOrderRequestDto requestDto,
            @AuthenticationPrincipal final User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final var result = this.service.createOrder(requestDto, user.getId());
        final var responseDto = result.getValue();
        if (responseDto != null) {
            final var location = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).findOrderById(responseDto.getId()))
                    .toUri();
            return ResponseEntity.created(location).body(responseDto);
        }

        return ResponseEntity.internalServerError().build();
    }
}
