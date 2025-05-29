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
import com.movie.main.dto.request.TicketTypeRequestDto;
import com.movie.main.dto.response.TicketTypeResponseDto;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.entity.TicketType;
import com.movie.main.service.TicketTypeService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/ticket-types")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class TicketTypeController {
    @NotNull
    private final TicketTypeService service;

    protected TicketTypeController(@NotNull final TicketTypeService service) {
        this.service = service;
    }

    @GetMapping
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<TicketTypeResponseDto>>> findAll(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            @NotNull final PagedResourcesAssembler<TicketTypeResponseDto> assembler) {
        final var result = this.service.findAll(PageRequest.of(page, size))
                .map(TicketTypeController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("{id}")
    @PermitAll
    public ResponseEntity<TicketTypeResponseDto> findById(@PathVariable final int id) {
        final var result = this.service.findById(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(TicketTypeController.getResponseDtoFrom(result));
    }

    @PostMapping
    public ResponseEntity<TicketTypeResponseDto> create(@RequestBody @Valid final TicketTypeRequestDto requestDto) {
        final var result = this.service.create(requestDto);
        final var newTicketType = result.getValue();

        if (newTicketType != null) {
            final var responseDto = TicketTypeController.getResponseDtoFrom(newTicketType);
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
    public ResponseEntity<TicketTypeResponseDto> updateById(
            @PathVariable final int id,
            @RequestBody @Valid final TicketTypeRequestDto requestDto) {
        final var result = this.service.updateById(id, requestDto);
        final var ticketType = result.getValue();

        if (ticketType != null) {
            return ResponseEntity.ok(TicketTypeController.getResponseDtoFrom(ticketType));
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
    public static TicketTypeResponseDto getResponseDtoFrom(@NotNull final TicketType ticketType) {
        return new TicketTypeResponseDto(
                ticketType.getId(),
                ticketType.getTitle(),
                ticketType.getPrice(),
                ticketType.isPair());
    }
}
