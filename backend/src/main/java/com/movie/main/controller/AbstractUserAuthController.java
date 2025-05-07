package com.movie.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.movie.main.dto.request.LoginRequestDto;
import com.movie.main.dto.request.UserDetailsRequestDtoInterface;
import com.movie.main.dto.response.AuthResponseDto;
import com.movie.main.dto.response.UserDetailsResponseDtoInterface;
import com.movie.main.entity.AbstractUserDetail;
import com.movie.main.service.AbstractUserAuthService;
import com.movie.main.service.UserDetailsServiceInterface;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@PermitAll
@Slf4j
public abstract class AbstractUserAuthController<
        TUserAuthService extends AbstractUserAuthService<TUserDetailsService, TUserDetailsRequestDto, TUserDetailsResponseDto, TUserDetails>,
        TUserDetailsService extends UserDetailsServiceInterface<TUserDetailsRequestDto, TUserDetailsResponseDto, TUserDetails>,
        TUserDetailsRequestDto extends UserDetailsRequestDtoInterface,
        TUserDetailsResponseDto extends UserDetailsResponseDtoInterface,
        TUserDetails extends AbstractUserDetail> {
    @PostMapping("/register")
    public ResponseEntity<TUserDetailsResponseDto> register(
            @RequestBody @Valid final TUserDetailsRequestDto requestDto) {
        final var result = this.getUserAuthService().register(requestDto);

        final var userDetailsResponseDto = result.getValue();
        if (userDetailsResponseDto != null) {
            return ResponseEntity.ok(userDetailsResponseDto);
        }

        return switch (result.getError()) {
        case UsernameExists -> ResponseEntity.status(HttpStatus.CONFLICT).build();
        case Unspecified -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid final LoginRequestDto requestDto) {
        final var result = this.getUserAuthService().login(requestDto);

        final var authResponseDto = result.getValue();
        if (authResponseDto != null) {
            return ResponseEntity.ok(authResponseDto);
        }

        return switch (result.getError()) {
        case UsernameNotExists, WrongPassword -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        case Unspecified -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @NotNull
    protected abstract TUserAuthService getUserAuthService();
}
