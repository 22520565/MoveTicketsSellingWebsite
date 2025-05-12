package com.movie.main.controller;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.auth.RequirePermission;
import com.movie.main.config.OpenApiConfig;
import com.movie.main.dto.request.CustomerRequestDto;
import com.movie.main.dto.response.CustomerResponseDto;
import com.movie.main.entity.Customer;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.exception.ConflictException;
import com.movie.main.service.CustomerService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/customers")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class CustomerController {
    @NotNull
    private final CustomerService service;

    protected CustomerController(@NotNull final CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<CustomerResponseDto>>> findAllByBlockedFalseAndDeletedFalse(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<CustomerResponseDto> assembler) {
        final var movies = this.service.findAllByBlockedFalseAndDeletedFalse(PageRequest.of(page, size))
                .map(CustomerController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(movies));
    }

    @GetMapping("blocked")
    public ResponseEntity<PagedModel<EntityModel<CustomerResponseDto>>> findAllByBlockedTrueAndDeletedFalse(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<CustomerResponseDto> assembler) {
        final var movies = this.service.findAllByBlockedTrueAndDeletedFalse(PageRequest.of(page, size))
                .map(CustomerController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(movies));
    }

    @GetMapping("deleted")
    public ResponseEntity<PagedModel<EntityModel<CustomerResponseDto>>> findAllByDeletedTrue(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<CustomerResponseDto> assembler) {
        final var movies = this.service.findAllByDeletedTrue(PageRequest.of(page, size))
                .map(CustomerController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(movies));
    }

    @GetMapping("{id}")
    public ResponseEntity<CustomerResponseDto> findByIdAndBlockedFalseAndDeletedFalse(@PathVariable final int id) {
        final var result = this.service.findByIdAndBlockedFalseAndDeletedFalse(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(CustomerController.getResponseDtoFrom(result));
    }

    @GetMapping("blocked/{id}")
    public ResponseEntity<CustomerResponseDto> findByIdAndBlockedTrueAndDeletedFalse(@PathVariable final int id) {
        final var result = this.service.findByIdAndBlockedTrueAndDeletedFalse(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(CustomerController.getResponseDtoFrom(result));
    }

    @GetMapping("deleted/{id}")
    public ResponseEntity<CustomerResponseDto> findByIdAndDeletedTrue(@PathVariable final int id) {
        final var result = this.service.findByIdAndDeletedTrue(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(CustomerController.getResponseDtoFrom(result));
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDto> create(
            @RequestBody @NotNull @Valid final CustomerRequestDto requestDto) {
        final var result = this.service.create(requestDto);
        final var newCustomer = result.getValue();

        if (newCustomer != null) {
            final var responseDto = CustomerController.getResponseDtoFrom(newCustomer);
            final var location = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                    .findByIdAndBlockedFalseAndDeletedFalse(responseDto.id())).toUri();

            return ResponseEntity.created(location).body(responseDto);
        }

        return switch (result.getError()) {
        case USERNAME_EXISTS -> throw new ConflictException("Username exists");
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PutMapping("{id}")
    public ResponseEntity<CustomerResponseDto> updateByIdAndDeletedFalse(@PathVariable final int id,
            @RequestBody @Valid final CustomerRequestDto requestDto) {
        final var result = this.service.updateByIdAndDeletedFalse(id, requestDto);
        final var customer = result.getValue();

        if (customer != null) {
            return ResponseEntity.ok(CustomerController.getResponseDtoFrom(customer));
        }

        return switch (result.getError()) {
        case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
        case USERNAME_EXISTS -> throw new ConflictException("Username exists");
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PatchMapping("block/{id}")
    public ResponseEntity<Void> markAsBlockedById(@PathVariable final int id) {
        return switch (this.service.markAsBlockedById(id)) {
        case SUCCESS -> ResponseEntity.noContent().build();
        case ENTITY_NOT_EXISTS_ERROR -> ResponseEntity.notFound().build();
        case UNSPECIFIED_ERROR -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PatchMapping("unblock/{id}")
    public ResponseEntity<Void> markAsUnblockedById(@PathVariable final int id) {
        return switch (this.service.markAsUnblockedById(id)) {
        case SUCCESS -> ResponseEntity.noContent().build();
        case ENTITY_NOT_EXISTS_ERROR -> ResponseEntity.notFound().build();
        case UNSPECIFIED_ERROR -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PatchMapping("delete/{id}")
    public ResponseEntity<Void> markAsDeletedById(@PathVariable final int id) {
        return switch (this.service.markAsDeletedById(id)) {
        case SUCCESS -> ResponseEntity.noContent().build();
        case ENTITY_NOT_EXISTS_ERROR -> ResponseEntity.notFound().build();
        case UNSPECIFIED_ERROR -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PatchMapping("undelete/{id}")
    public ResponseEntity<Void> markAsUndeletedById(@PathVariable final int id) {
        return switch (this.service.markAsUndeletedById(id)) {
        case SUCCESS -> ResponseEntity.noContent().build();
        case ENTITY_NOT_EXISTS_ERROR -> ResponseEntity.notFound().build();
        case UNSPECIFIED_ERROR -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @NotNull
    public static CustomerResponseDto getResponseDtoFrom(@NotNull final Customer customer) {
        return new CustomerResponseDto(customer.getId(), customer.getName(), customer.getBirthDate(),
                customer.getEmail(), customer.getPhoneNumber(), customer.getUsername());
    }
}
