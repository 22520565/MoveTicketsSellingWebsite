package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.AgeRestrictionRequestDto;
import com.movie.main.entity.AgeRestriction;
import com.movie.main.repository.AgeRestrictionRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AgeRestrictionService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    @NotNull
    private final AgeRestrictionRepository repository;

    public AgeRestrictionService(@NotNull final AgeRestrictionRepository repository) {
        this.repository = repository;
    }

    @NotNull
    public Page<@NotNull AgeRestriction> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public AgeRestriction findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<AgeRestriction, CreationError> create(@NotNull final AgeRestrictionRequestDto requestDto) {
        final var newAgeRestriction = new AgeRestriction(requestDto.name());

        try {
            return Expected.success(this.repository.save(newAgeRestriction));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<AgeRestriction, UpdateError> updateById(final int id,
            @NotNull final AgeRestrictionRequestDto requestDto) {
        final var ageRestriction = this.findById(id);
        if (ageRestriction == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        ageRestriction.setName(requestDto.name());

        try {
            return Expected.success(this.repository.save(ageRestriction));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public void deleteById(final int id) {
        this.repository.deleteById(id);
    }
}
