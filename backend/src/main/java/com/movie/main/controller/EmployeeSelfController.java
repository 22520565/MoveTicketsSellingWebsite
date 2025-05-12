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

import com.movie.main.dto.request.EmployeeRequestDto;
import com.movie.main.dto.request.ResetPasswordRequestDto;
import com.movie.main.dto.response.EmployeeResponseDto;
import com.movie.main.entity.User;
import com.movie.main.exception.ConflictException;
import com.movie.main.service.EmployeeService;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/self/employee")
@PermitAll
public class EmployeeSelfController {
    @NotNull
    private final EmployeeService service;

    public EmployeeSelfController(@NotNull final EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<EmployeeResponseDto> findSelfInfo(@AuthenticationPrincipal final User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final var employee = service.findByIdAndBlockedFalseAndDeletedFalse(user.getId());
        if (employee != null) {
            return ResponseEntity.ok(EmployeeController.getResponseDtoFrom(employee));
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping
    public ResponseEntity<EmployeeResponseDto> updateSelfInfo(@RequestBody @Valid final EmployeeRequestDto requestDto,
            @AuthenticationPrincipal final User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final var result = service.updateByIdAndDeletedFalse(user.getId(), requestDto);
        final var employee = result.getValue();
        if (employee != null) {
            return ResponseEntity.ok(EmployeeController.getResponseDtoFrom(result.getValue()));
        }

        return switch (result.getError()) {
        case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
        case USERNAME_EXISTS -> throw new ConflictException("Username exists");
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid final ResetPasswordRequestDto requestDto,
            @AuthenticationPrincipal final User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final var result = service.resetPasswordAndDeletedFalse(user.getId(), requestDto.oldPassword(),
                requestDto.newPassword());
        return switch (result) {
        case SUCCESS -> ResponseEntity.noContent().build();
        case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
        case WRONG_OLD_PASSWORD -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        default -> ResponseEntity.internalServerError().build();
        };
    }
}
