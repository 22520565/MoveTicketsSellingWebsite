package com.movie.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.CustomerSelfRequestDto;
import com.movie.main.dto.request.ResetPasswordRequestDto;
import com.movie.main.dto.response.CustomerResponseDto;
import com.movie.main.entity.User;
import com.movie.main.exception.ConflictException;
import com.movie.main.service.CustomerService;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/self/customer")
@PermitAll
public class CustomerSelfController {
    @NotNull
    private final CustomerService service;

    public CustomerSelfController(@NotNull final CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<CustomerResponseDto> findSelfInfo(@AuthenticationPrincipal final User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final var customer = service.findByIdAndBlockedFalseAndDeletedFalse(user.getId());
        if (customer != null) {
            return ResponseEntity.ok(CustomerController.getResponseDtoFrom(customer));
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping
    public ResponseEntity<CustomerResponseDto> updateSelfInfo(
            @RequestBody @Valid final CustomerSelfRequestDto requestDto, @AuthenticationPrincipal final User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final var result = service.updateByIdAndBlockedFalseAndDeletedFalse(user.getId(), requestDto);
        final var customer = result.getValue();
        if (customer != null) {
            return ResponseEntity.ok(CustomerController.getResponseDtoFrom(result.getValue()));
        }

        return switch (result.getError()) {
        case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
        case USERNAME_EXISTS -> throw new ConflictException("Username exists");
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Void> resetSelfPassword(@RequestBody @Valid final ResetPasswordRequestDto requestDto,
            @AuthenticationPrincipal final User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final var result = service.resetPasswordByIdAndBlockedFalseAndDeletedFalse(user.getId(),
                requestDto.oldPassword(), requestDto.newPassword());
        return switch (result) {
        case SUCCESS -> ResponseEntity.noContent().build();
        case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
        case WRONG_OLD_PASSWORD -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        default -> ResponseEntity.internalServerError().build();
        };
    }
}
