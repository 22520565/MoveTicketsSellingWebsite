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
import com.movie.main.dto.request.AgeRestrictionRequestDto;
import com.movie.main.dto.response.AgeRestrictionResponseDto;
import com.movie.main.entity.AgeRestriction;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.service.AgeRestrictionService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/age-restrictions")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class AgeRestrictionController {
    @NotNull
    private final AgeRestrictionService service;

    protected AgeRestrictionController(@NotNull final AgeRestrictionService service) {
        this.service = service;
    }

    @GetMapping
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<AgeRestrictionResponseDto>>> findAll(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<AgeRestrictionResponseDto> assembler) {
        final var result = this.service.findAll(PageRequest.of(page, size))
                .map(AgeRestrictionController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("{id}")
    @PermitAll
    public ResponseEntity<AgeRestrictionResponseDto> findById(@PathVariable final int id) {
        final var result = this.service.findById(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(AgeRestrictionController.getResponseDtoFrom(result));
    }

    @PostMapping
    public ResponseEntity<AgeRestrictionResponseDto> create(
            @RequestBody @Valid final AgeRestrictionRequestDto requestDto) {
        final var result = this.service.create(requestDto);
        final var newAgeRestriction = result.getValue();

        if (newAgeRestriction != null) {
            final var responseDto = AgeRestrictionController.getResponseDtoFrom(newAgeRestriction);
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
    public ResponseEntity<AgeRestrictionResponseDto> updateById(
            @PathVariable final int id,
            @RequestBody @Valid final AgeRestrictionRequestDto requestDto) {
        final var result = this.service.updateById(id, requestDto);
        final var ageRestriction = result.getValue();

        if (ageRestriction != null) {
            return ResponseEntity.ok(AgeRestrictionController.getResponseDtoFrom(ageRestriction));
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
    public static AgeRestrictionResponseDto getResponseDtoFrom(@NotNull final AgeRestriction ageRestriction) {
        return new AgeRestrictionResponseDto(
                ageRestriction.getId(),
                ageRestriction.getName());
    }
}
