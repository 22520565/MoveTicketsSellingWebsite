package com.movie.main.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.movie.main.dto.request.UserDetailsRequestDtoInterface;
import com.movie.main.dto.response.UserDetailsResponseDtoInterface;
import com.movie.main.entity.UserDetailsInterface;
import com.movie.main.repository.UserDetailsRepository;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public interface UserDetailsServiceInterface<TUserDetailsRequestDto extends UserDetailsRequestDtoInterface,
        TUserDetailsResponseDto extends UserDetailsResponseDtoInterface,
        TUserDetails extends UserDetailsInterface> {
    @Nullable
    default TUserDetails findEntityByUsername(@Nullable final String username) {
        return this.getRepository().findByUserUsername(username).orElse(null);
    }

    @Nullable
    default TUserDetailsResponseDto findByUsername(@Nullable final String username) {
        final TUserDetails user = this.findEntityByUsername(username);
        if (user == null) {
            return null;
        }

        return this.createResponseDtoFromEntity(user);
    }

    default boolean existByUsername(@Nullable final String username) {
        return this.getRepository().existsByUserUsername(username);
    }

    @NotNull
    TUserDetailsResponseDto createResponseDtoFromEntity(@NotNull final TUserDetails userDetails);

    @Nullable
    TUserDetailsResponseDto create(@NotNull final TUserDetailsRequestDto requestDto);

    @NotNull
    UserDetailsRepository<TUserDetails> getRepository();
}
