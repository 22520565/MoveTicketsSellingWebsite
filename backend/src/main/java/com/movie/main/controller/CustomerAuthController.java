package com.movie.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.CustomerRequestDto;
import com.movie.main.dto.request.LoginRequestDto;
import com.movie.main.dto.request.LogoutRequestDto;
import com.movie.main.dto.request.TokenRefreshRequestDto;
import com.movie.main.dto.response.CustomerResponseDto;
import com.movie.main.dto.response.LoginResponseDto;
import com.movie.main.dto.response.TokenRefreshResponseDto;
import com.movie.main.exception.ConflictException;
import com.movie.main.service.CustomerAuthService;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/auth/customer")
@PermitAll
public class CustomerAuthController {
    @NotNull
    private final CustomerAuthService customerAuthService;

    public CustomerAuthController(@NotNull final CustomerAuthService customerAuthService) {
        this.customerAuthService = customerAuthService;
    }

    @PostMapping("register")
    public ResponseEntity<CustomerResponseDto> register(@RequestBody @Valid final CustomerRequestDto requestDto) {
        final var result = this.customerAuthService.register(requestDto);

        final var customer = result.getValue();
        if (customer != null) {
            return ResponseEntity.ok(CustomerController.getResponseDtoFrom(customer));
        }

        return switch (result.getError()) {
        case USERNAME_EXISTS -> throw new ConflictException("Username exists");
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid final LoginRequestDto requestDto) {
        final var result = this.customerAuthService.login(requestDto);

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

    @PostMapping("refresh-token")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(
            @RequestBody @Valid final TokenRefreshRequestDto requestDto) {
        final var result = this.customerAuthService.refreshToken(requestDto);

        final var responseDto = result.getValue();
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid final LogoutRequestDto requestDto) {
        this.customerAuthService.logout(requestDto.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
