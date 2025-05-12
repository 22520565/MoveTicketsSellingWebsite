package com.movie.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.LoginRequestDto;
import com.movie.main.dto.request.LogoutRequestDto;
import com.movie.main.dto.request.TokenRefreshRequestDto;
import com.movie.main.dto.response.LoginResponseDto;
import com.movie.main.dto.response.TokenRefreshResponseDto;
import com.movie.main.service.EmployeeAuthService;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/auth/employee")
@PermitAll
public class EmployeeAuthController {
    @NotNull
    private final EmployeeAuthService employeeAuthService;

    public EmployeeAuthController(@NotNull final EmployeeAuthService employeeAuthService) {
        this.employeeAuthService = employeeAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid final LoginRequestDto requestDto) {
        final var result = this.employeeAuthService.login(requestDto);

        final var authResponseDto = result.getValue();
        if (authResponseDto != null) {
            return ResponseEntity.ok(authResponseDto);
        }

        return switch (result.getError()) {
        case USERNAME_NOT_EXISTS, WRONG_PASSWORD -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        case BLOCKED -> ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(
            @RequestBody @Valid final TokenRefreshRequestDto requestDto) {
        final var result = this.employeeAuthService.refreshToken(requestDto);

        final var responseDto = result.getValue();
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid final LogoutRequestDto requestDto) {
        this.employeeAuthService.logout(requestDto.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
