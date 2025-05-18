package com.movie.main.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.movie.main.auth.JwtTokenProvider;
import com.movie.main.dto.request.LoginRequestDto;
import com.movie.main.dto.request.TokenRefreshRequestDto;
import com.movie.main.dto.response.LoginResponseDto;
import com.movie.main.dto.response.TokenRefreshResponseDto;
import com.movie.main.entity.User.UserRole;
import com.movie.main.ulti.Expected;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeAuthService {
    public enum RegisterError {
        USERNAME_EXISTS, UNSPECIFIED,
    }

    public enum LoginError {
        USERNAME_NOT_EXISTS, WRONG_PASSWORD, BLOCKED, UNSPECIFIED,
    }

    public enum RefreshTokenError {
        NOT_FOUND, EXPRIED,
    }

    @NotNull
    private final EmployeeService employeeService;

    @NotNull
    private final UserRefreshTokenService userRefreshTokenService;

    @NotNull
    private final PasswordEncoder passwordEncoder;

    public EmployeeAuthService(@NotNull final EmployeeService employeeService,
            @NotNull final UserRefreshTokenService userRefreshTokenService,
            @NotNull final PasswordEncoder passwordEncoder) {
        this.employeeService = employeeService;
        this.userRefreshTokenService = userRefreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @NotNull
    public Expected<LoginResponseDto, LoginError> login(@NotNull final LoginRequestDto requestDto) {
        try {
            final var employee = this.employeeService.findByUsernameAndDeletedFalse(requestDto.username());
            if (employee == null) {
                return Expected.failure(LoginError.USERNAME_NOT_EXISTS);
            }

            if (employee.isBlocked()) {
                return Expected.failure(LoginError.BLOCKED);
            }

            if (!this.passwordEncoder.matches(requestDto.password(), employee.getHashedPassword())) {
                return Expected.failure(LoginError.WRONG_PASSWORD);
            }

            final var accessToken = JwtTokenProvider.generateToken(requestDto.username(), UserRole.EMPLOYEE);
            final var refreshToken = this.userRefreshTokenService.createRefreshToken(employee).getId();

            return Expected.success(new LoginResponseDto(accessToken, refreshToken));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(LoginError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<TokenRefreshResponseDto, RefreshTokenError> refreshToken(final TokenRefreshRequestDto requestDto) {
        final var oldRefreshToken = requestDto.token();

        final var oldUserRefreshToken = userRefreshTokenService.findById(oldRefreshToken);
        if (oldUserRefreshToken == null) {
            return Expected.failure(RefreshTokenError.NOT_FOUND);
        }

        if (!userRefreshTokenService.isUserRefreshTokenValid(oldUserRefreshToken)) {
            return Expected.failure(RefreshTokenError.EXPRIED);
        }

        final var newUserRefreshToken = userRefreshTokenService.createRefreshToken(oldUserRefreshToken);
        final var accessToken = JwtTokenProvider.generateToken(newUserRefreshToken.getUser().getUsername(),
                UserRole.EMPLOYEE);
        final var responseDto = new TokenRefreshResponseDto(accessToken, newUserRefreshToken.getId());

        return Expected.success(responseDto);
    }

    public void logout(@NotNull final UUID refreshToken) {
        this.userRefreshTokenService.deleteById(refreshToken);
    }
}
