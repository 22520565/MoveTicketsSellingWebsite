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
import com.movie.main.dto.request.OrderDecoratorsPromotionRequestDto;
import com.movie.main.dto.response.OrderDecoratorsPromotionResponseDto;
import com.movie.main.entity.OrderDecoratorsPromotion;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.service.OrderDecoratorsPromotionService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/order-decorators-promotions")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class OrderDecoratorsPromotionController {
    @NotNull
    private final OrderDecoratorsPromotionService service;

    protected OrderDecoratorsPromotionController(@NotNull final OrderDecoratorsPromotionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<OrderDecoratorsPromotionResponseDto>>> findAll(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<OrderDecoratorsPromotionResponseDto> assembler) {
        final var result = this.service.findAll(PageRequest.of(page, size))
                .map(OrderDecoratorsPromotionController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderDecoratorsPromotionResponseDto> findById(@PathVariable final int id) {
        final var result = this.service.findById(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(OrderDecoratorsPromotionController.getResponseDtoFrom(result));
    }

    @PostMapping
    public ResponseEntity<OrderDecoratorsPromotionResponseDto> create(
            @RequestBody @Valid final OrderDecoratorsPromotionRequestDto requestDto) {
        final var result = this.service.create(requestDto);
        final var newOrderDecoratorsPromotion = result.getValue();

        if (newOrderDecoratorsPromotion != null) {
            final var responseDto = OrderDecoratorsPromotionController.getResponseDtoFrom(newOrderDecoratorsPromotion);
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
    public ResponseEntity<OrderDecoratorsPromotionResponseDto> updateById(@PathVariable final int id,
            @RequestBody @Valid final OrderDecoratorsPromotionRequestDto requestDto) {
        final var result = this.service.updateById(id, requestDto);
        final var orderDecoratorsPromotion = result.getValue();

        if (orderDecoratorsPromotion != null) {
            return ResponseEntity.ok(OrderDecoratorsPromotionController.getResponseDtoFrom(orderDecoratorsPromotion));
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
    public static OrderDecoratorsPromotionResponseDto getResponseDtoFrom(
            @NotNull final OrderDecoratorsPromotion orderDecoratorsPromotion) {
        return new OrderDecoratorsPromotionResponseDto(orderDecoratorsPromotion.getCustomerOrder().getId(),
                orderDecoratorsPromotion.getPromotions());
    }
}
