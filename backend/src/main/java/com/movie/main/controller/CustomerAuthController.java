package com.movie.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.config.JwtTokenProvider;
import com.movie.main.dto.request.CustomerRequestDto;
import com.movie.main.dto.request.LoginRequestDto;
import com.movie.main.dto.request.UserRequestDto;
import com.movie.main.dto.response.AuthResponse;
import com.movie.main.dto.response.CustomerResponseDto;
import com.movie.main.service.CustomerService;
import com.movie.main.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer-auth")
public class CustomerAuthController {
    private final CustomerService userService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public CustomerAuthController(
            final CustomerService userService,
            final JwtTokenProvider tokenProvider,
            final PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerResponseDto> register(@RequestBody @Valid final CustomerRequestDto request) {
        if (userService.existByUsername(request.userRequestDto().username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        final var userResponseDto = userService.create(request);
        if (userResponseDto == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid final LoginRequestDto request) {
        final var customer = userService.findEntityByUsername(request.username());
        if (customer == null || !passwordEncoder.matches(request.password(), customer.getUser().getHashedPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final String token = tokenProvider.generateToken(request.username());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
