package com.movie.main.controller;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.auth.RequirePermission;
import com.movie.main.config.OpenApiConfig;
import com.movie.main.dto.request.OrderRequestDto;
import com.movie.main.dto.response.OrderResponseDto;
import com.movie.main.dto.response.StripePaymentCreateIntentResponseDto;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.entity.User;
import com.movie.main.service.OrderService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("orders")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class OrderController {
    @NotNull
    private final OrderService service;

    public OrderController(@NotNull final OrderService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<OrderResponseDto>>> findAll(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<OrderResponseDto> assembler) {
        final var result = this.service.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("{id}")
    @PermitAll
    public ResponseEntity<OrderResponseDto> findById(@PathVariable final int id) {
        final var responseDto = this.service.findById(id);
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        }

        return ResponseEntity.internalServerError().build();
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> create(
            @Valid final OrderRequestDto requestDto,
            @AuthenticationPrincipal final User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final var result = this.service.create(requestDto, user.getId());
        final var responseDto = result.getValue();
        if (responseDto != null) {
            final var location = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).findById(responseDto.getId()))
                    .toUri();
            return ResponseEntity.created(location).body(responseDto);
        }

        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("stripe-intent")
    @PermitAll
    public ResponseEntity<StripePaymentCreateIntentResponseDto> createStripePaymentIntent(
            @RequestBody OrderRequestDto requestDto,
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
    @PermitAll
    public ResponseEntity<OrderResponseDto> handleStripeWebhook(
            @RequestHeader("Stripe-Signature") String sigHeader,
            @RequestBody String payload) {
        final var result = this.service.handleStripeWebhook(sigHeader, payload);

        final var responseDto = result.getValue();
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        }

        return ResponseEntity.internalServerError().build();
    }
}
