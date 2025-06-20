package com.movie.main.controller;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.auth.RequirePermission;
import com.movie.main.config.OpenApiConfig;
import com.movie.main.dto.request.OrderDataFilmRequestDto;
import com.movie.main.dto.response.OrderDataFilmResponseDto;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.entity.OrderDataFilm;
import com.movie.main.service.OrderDataFilmService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/order-data-films")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class OrderDataFilmController {
    @NotNull
    private final OrderDataFilmService service;

    protected OrderDataFilmController(@NotNull final OrderDataFilmService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<OrderDataFilmResponseDto>>> findAll(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<OrderDataFilmResponseDto> assembler) {
        final var result = this.service.findAll(PageRequest.of(page, size))
                .map(OrderDataFilmController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderDataFilmResponseDto> findById(@PathVariable final int id) {
        final var result = this.service.findById(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(OrderDataFilmController.getResponseDtoFrom(result));
    }

    @PostMapping
    public ResponseEntity<OrderDataFilmResponseDto> create(
            @RequestBody @Valid final OrderDataFilmRequestDto requestDto) {
        final var result = this.service.create(requestDto);
        final var newOrderDataFilm = result.getValue();

        if (newOrderDataFilm != null) {
            final var responseDto = OrderDataFilmController.getResponseDtoFrom(newOrderDataFilm);
            final var location = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).findById(responseDto.customerOrderId()))
                    .toUri();

            return ResponseEntity.created(location).body(responseDto);
        }

        return switch (result.getError()) {
            case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
            case UNSPECIFIED -> ResponseEntity.internalServerError().build();
            default -> ResponseEntity.internalServerError().build();
        };
    }

    @PutMapping("{id}")
    public ResponseEntity<OrderDataFilmResponseDto> updateById(
            @PathVariable final int id,
            @RequestBody @Valid final OrderDataFilmRequestDto requestDto) {
        final var result = this.service.updateById(id, requestDto);
        final var orderDataFilm = result.getValue();

        if (orderDataFilm != null) {
            return ResponseEntity.ok(OrderDataFilmController.getResponseDtoFrom(orderDataFilm));
        }

        return switch (result.getError()) {
            case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
            case UNSPECIFIED -> ResponseEntity.internalServerError().build();
            default -> ResponseEntity.internalServerError().build();
        };
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable final int id) {
        this.service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @NotNull
    public static OrderDataFilmResponseDto getResponseDtoFrom(@NotNull final OrderDataFilm orderDataFilm) {
        return new OrderDataFilmResponseDto(
                orderDataFilm.getCustomerOrder().getId(),
                orderDataFilm.getDate(),
                orderDataFilm.getTime(),
                orderDataFilm.getFilmShow(),
                orderDataFilm.getVerifyCode(),
                orderDataFilm.getRoomSeats(),
                orderDataFilm.getOrderTickets());
    }
}
