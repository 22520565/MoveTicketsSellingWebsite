package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.movie.main.dto.FilmRequestDto;
import com.movie.main.entity.Film;
import com.movie.main.entity.FilmShow;
import com.movie.main.entity.Identifiable;
import com.movie.main.repository.InterfaceRepository;
import com.movie.main.ulti.Expected;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractService<TRequestDto, TEntity extends Identifiable<TKey>, TKey> {
    public enum DeletionStatus {
        Success,
        EntityNotExistsError,
        UnspecifiedError,
    }

    public enum UpdateError {
        EntityNotExists,
        Unspecified,
    }

    @Nullable
    public TEntity findById(@NotNull final TKey id) {
        try {
            return this.getRepository().findById(id).orElse(null);
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    @NotNull
    public Page<TEntity> findAll(@NotNull final PageRequest pageRequest) {
        return this.getRepository().findAll(pageRequest);
    }

    public abstract TEntity create(@NotNull final TRequestDto requestDto);

    public abstract Expected<TEntity, UpdateError> update(
            @NotNull final TKey id,
            @NotNull final TRequestDto requestDto);

    @Nullable
    protected TEntity save(@NotNull final TEntity entity) {
        try {
            return this.getRepository().save(entity);
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    public DeletionStatus deleteById(@NotNull final TKey id) {
        try {
            final var repository = this.getRepository();

            if (!repository.existsById(id)) {
                return DeletionStatus.EntityNotExistsError;
            }

            repository.deleteById(id);
            return DeletionStatus.Success;
        } catch (final Exception exception) {
            log.error(null, exception);
            return DeletionStatus.UnspecifiedError;
        }
    }

    @NotNull
    protected abstract InterfaceRepository<TEntity, TKey> getRepository();
}
