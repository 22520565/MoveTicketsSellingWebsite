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
import com.movie.main.dto.request.PromotionRequestDto;
import com.movie.main.dto.response.PromotionResponseDto;
import com.movie.main.entity.Promotion;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.service.PromotionService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/promotions")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class PromotionController {
    @NotNull
    private final PromotionService service;

    protected PromotionController(@NotNull final PromotionService service) {
        this.service = service;
    }

    @GetMapping
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<PromotionResponseDto>>> findAll(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<PromotionResponseDto> assembler) {
        final var result = this.service.findAll(PageRequest.of(page, size))
                .map(PromotionController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("{id}")
    @PermitAll
    public ResponseEntity<PromotionResponseDto> findById(@PathVariable final int id) {
        final var result = this.service.findById(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PromotionController.getResponseDtoFrom(result));
    }

    @PostMapping
    public ResponseEntity<PromotionResponseDto> create(@RequestBody @Valid final PromotionRequestDto requestDto) {
        final var result = this.service.create(requestDto);
        final var newPromotion = result.getValue();

        if (newPromotion != null) {
            final var responseDto = PromotionController.getResponseDtoFrom(newPromotion);
            final var location = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).findById(responseDto.id()))
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
    public ResponseEntity<PromotionResponseDto> updateById(
            @PathVariable final int id,
            @RequestBody @Valid final PromotionRequestDto requestDto) {
        final var result = this.service.updateById(id, requestDto);
        final var promotion = result.getValue();

        if (promotion != null) {
            return ResponseEntity.ok(PromotionController.getResponseDtoFrom(promotion));
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
    public static PromotionResponseDto getResponseDtoFrom(@NotNull final Promotion promotion) {
        return new PromotionResponseDto(promotion.getId(), promotion.getName(), promotion.getThumbnailUrl(),
                promotion.getDiscountRate(), promotion.getBeginDate(), promotion.getEndDate());
    }
}
