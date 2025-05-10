package com.movie.main.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.movie.main.auth.JwtTokenProvider;
import com.movie.main.dto.request.LoginRequestDto;
import com.movie.main.dto.request.TokenRefreshRequestDto;
import com.movie.main.dto.request.UserDetailsRequestDtoInterface;
import com.movie.main.dto.response.LoginResponseDto;
import com.movie.main.dto.response.TokenRefreshResponseDto;
import com.movie.main.dto.response.UserDetailsResponseDtoInterface;
import com.movie.main.entity.AbstractUserDetail;
import com.movie.main.entity.AbstractUserDetail.UserRole;
import com.movie.main.ulti.Expected;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractUserAuthService<
        TUserDetailsService extends AbstractUserDetailsService<TUserDetailsRequestDto, TUserDetailsResponseDto, TUserDeatails>,
        TUserDetailsRequestDto extends UserDetailsRequestDtoInterface,
        TUserDetailsResponseDto extends UserDetailsResponseDtoInterface,
        TUserDeatails extends AbstractUserDetail> {
    public enum RegisterError {
        UsernameExists, Unspecified,
    }

    public enum LoginError {
        UsernameNotExists, WrongPassword, Blocked, Unspecified,
    }

    public enum RefreshTokenError {
        NOT_FOUND, EXPRIED,
    }

    @NotNull
    public Expected<LoginResponseDto, LoginError> login(@NotNull final LoginRequestDto requestDto) {
        try {
            final var userEntity = this.getUserDetailsService()
                    .getRepository()
                    .findByUserUsernameAndDeletedFalse(requestDto.username())
                    .orElse(null);
            if (userEntity == null) {
                return Expected.failure(LoginError.UsernameNotExists);
            }

            final var user = userEntity.getUser();
            if (user.isBlocked()) {
                return Expected.failure(LoginError.Blocked);
            }

            if (!this.getPasswordEncoder().matches(requestDto.password(), user.getHashedPassword())) {
                return Expected.failure(LoginError.WrongPassword);
            }

            final var accessToken = JwtTokenProvider.generateToken(requestDto.username(), this.getUserRole());
            final var refreshToken = this.getUserRefreshTokenService().createRefreshToken(userEntity.getUser()).getId();

            return Expected.success(new LoginResponseDto(userEntity.getId(), accessToken, refreshToken));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(LoginError.Unspecified);
        }
    }

    @NotNull
    public Expected<TUserDetailsResponseDto, RegisterError> register(@NotNull final TUserDetailsRequestDto requestDto) {
        try {
            if (this.getUserDetailsService().existByUsername(requestDto.userRequestDto().username())) {
                return Expected.failure(RegisterError.UsernameExists);
            }

            return Expected.success(this.getUserDetailsService().create(requestDto));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(RegisterError.Unspecified);
        }
    }

    @NotNull
    public Expected<TokenRefreshResponseDto, RefreshTokenError> refreshToken(final TokenRefreshRequestDto requestDto) {
        final var userRefreshTokenService = this.getUserRefreshTokenService();
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
                this.getUserRole());
        final var responseDto = new TokenRefreshResponseDto(accessToken, newUserRefreshToken.getId());

        return Expected.success(responseDto);
    }

    public void logout(@NotNull final UUID refreshToken) {
        this.getUserRefreshTokenService().deleteById(refreshToken);
    }

    @NotNull
    protected abstract TUserDetailsService getUserDetailsService();

    @NotNull
    protected abstract UserRefreshTokenService getUserRefreshTokenService();

    @NotNull
    protected abstract PasswordEncoder getPasswordEncoder();

    @NotNull
    public abstract UserRole getUserRole();
}
