package com.movie.main.service;

import com.movie.main.dto.request.UserDetailsRequestDtoInterface;
import com.movie.main.dto.response.UserDetailsResponseDtoInterface;
import com.movie.main.entity.AbstractUserDetail;
import com.movie.main.repository.UserDetailsRepository;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public abstract class AbstractUserDetailsService<TUserDetailsRequestDto extends UserDetailsRequestDtoInterface,
        TUserDetailsResponseDto extends UserDetailsResponseDtoInterface,
        TUserDetails extends AbstractUserDetail>
        extends AbstractEntityService<TUserDetailsRequestDto, TUserDetailsResponseDto, TUserDetails, Integer> {
    @Nullable
    public TUserDetails findEntityByUsername(@Nullable final String username) {
        return this.getRepository().findByUserUsernameAndDeletedFalse(username).orElse(null);
    }

    @Nullable
    public TUserDetailsResponseDto findByUsername(@Nullable final String username) {
        final var user = this.findEntityByUsername(username);
        if (user == null) {
            return null;
        }

        return this.createResponseDtoFromEntity(user);
    }

    public boolean existByUsername(@Nullable final String username) {
        return this.getRepository().existsByUserUsernameAndDeletedFalse(username);
    }

    @NotNull
    protected abstract UserDetailsRepository<TUserDetails> getRepository();
}
