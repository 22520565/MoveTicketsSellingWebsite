package com.movie.main.service;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.movie.main.dto.request.InterfaceRequestDto;
import com.movie.main.dto.response.InterfaceResponseDto;
import com.movie.main.entity.Identifiable;
import com.movie.main.repository.InterfaceRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractService<TRequestDto extends InterfaceRequestDto, TResponseDto extends InterfaceResponseDto, TEntity extends Identifiable<TKey>, TKey> {
    public enum DeletionStatus {
        Success,
        EntityNotExistsError,
        UnspecifiedError,
    }

    public enum UpdateError {
        EntityNotExists,
        Unspecified,
    }

    @NotNull
    protected static Logger getLogger() {
        return log;
    }

    @Nullable
    public TEntity findEntityById(@NotNull final TKey id) {
        try {
            return this.getRepository().findById(id).orElse(null);
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    @Nullable
    public TResponseDto findById(@NotNull final TKey id) {
        final var entity = this.findEntityById(id);

        if (entity == null) {
            return null;
        }

        return this.createResponseDtoFromEntity(entity);
    }

    @NotNull
    public Page<TResponseDto> findAll(@NotNull final PageRequest pageRequest) {
        return this.findAllEntities(pageRequest).map(this::createResponseDtoFromEntity);
    }

    @NotNull
    public Page<TEntity> findAllEntities(@NotNull final PageRequest pageRequest) {
        return this.getRepository().findAll(pageRequest);
    }

    @Nullable
    public TResponseDto create(@NotNull final TRequestDto requestDto) {
        var newEntity = this.createEntityFromRequestDto(requestDto);
        if (newEntity == null) {
            return null;
        }

        newEntity = this.save(newEntity);
        if (newEntity == null) {
            return null;
        }

        return this.createResponseDtoFromEntity(newEntity);
    }

    @NotNull
    public Expected<TResponseDto, UpdateError> update(
            @NotNull final TKey id,
            @NotNull final TRequestDto requestDto) {
        var entity = this.findEntityById(id);
        if (entity == null) {
            return Expected.failure(UpdateError.EntityNotExists);
        }

        entity = this.updateEntityFromRequestDto(entity, requestDto);
        if (entity == null) {
            return Expected.failure(UpdateError.EntityNotExists);
        }

        entity = this.save(entity);
        if (entity == null) {
            return Expected.failure(UpdateError.Unspecified);
        }

        return Expected.success(this.createResponseDtoFromEntity(entity));
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
    protected abstract TResponseDto createResponseDtoFromEntity(@NotNull final TEntity entity);

    @Nullable
    protected abstract TEntity createEntityFromRequestDto(@NotNull final TRequestDto requestDto);

    @Nullable
    protected abstract TEntity updateEntityFromRequestDto(
            @NotNull final TEntity entity,
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

    @NotNull
    protected abstract InterfaceRepository<TEntity, TKey> getRepository();
}
