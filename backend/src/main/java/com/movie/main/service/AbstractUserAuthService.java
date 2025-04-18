package com.movie.main.service;

import org.slf4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.movie.main.config.JwtTokenProvider;
import com.movie.main.dto.request.LoginRequestDto;
import com.movie.main.dto.request.UserDetailsRequestDtoInterface;
import com.movie.main.dto.response.UserDetailsResponseDtoInterface;
import com.movie.main.entity.UserDetailsInterface;
import com.movie.main.ulti.Expected;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractUserAuthService<TUserDetailsService extends UserDetailsServiceInterface<TUserDetailsRequestDto, TUserDetailsResponseDto, TUserDeatails>, TUserDetailsRequestDto extends UserDetailsRequestDtoInterface, TUserDetailsResponseDto extends UserDetailsResponseDtoInterface, TUserDeatails extends UserDetailsInterface> {
    public enum RegisterError {
        UsernameExists,
        Unspecified,
    }

    public enum LoginError {
        UsernameNotExists,
        WrongPassword,
        Unspecified,
    }

    @NotNull
    protected static Logger getLogger() {
        return log;
    }

    @NotNull
    public Expected<String, LoginError> login(@NotNull final LoginRequestDto requestDto) {
        try {
            final var userEntity = this.getUserDetailsService().getRepository()
                    .findByUserUsername(requestDto.username())
                    .orElse(null);
            if (userEntity == null) {
                return Expected.failure(LoginError.UsernameNotExists);
            }

            if (!this.getPasswordEncoder().matches(requestDto.password(), userEntity.getUser().getHashedPassword())) {
                return Expected.failure(LoginError.WrongPassword);
            }

            return Expected.success(this.getJwtTokenProvider().generateToken(requestDto.username()));
        } catch (final Exception exception) {
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
        } catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(RegisterError.Unspecified);
        }
    }

    @NotNull
    protected abstract TUserDetailsService getUserDetailsService();

    @NotNull
    protected abstract PasswordEncoder getPasswordEncoder();

    @NotNull
    protected abstract JwtTokenProvider getJwtTokenProvider();
}
