package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.movie.main.dto.InterfaceDto;
import com.movie.main.entity.Identifiable;
import com.movie.main.repository.AbstractRepository;
import com.movie.main.service.enumclass.DeletionStatus;
import com.movie.main.service.enumclass.UpdateStatus;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractService<TEntity extends Identifiable<TKey>, TDto extends InterfaceDto, TKey> {
    @NotNull
    public Page<TDto> findAllData(@NotNull final PageRequest pageRequest) {
        return this.getRepository().findAllData(pageRequest);
    }

    @Nullable
    public TEntity findById(@NotNull final TKey id) {
        return this.getRepository().findById(id);
    }

    @Nullable
    public TDto findDataById(@NotNull final TKey id) {
        return this.getRepository().findDataById(id);
    }

    @Nullable
    public abstract TEntity create(@NotNull final TDto dto);

    @Nullable
    public TEntity create(@NotNull final TEntity entity) {
        try {
            final var result = this.getRepository().add(entity);

            return result.getValue();
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    @Nullable
    public abstract UpdateStatus update(@NotNull final TKey id, @NotNull final TDto dto);

    @NotNull
    public UpdateStatus update(@NotNull final TEntity entity) {
        try {
            final var result = this.getRepository().update(entity);
            if (result.isSuccess()) {
                return UpdateStatus.Success;
            }

            return switch (result.getError()) {
                case EntityNotExists -> UpdateStatus.EntityNotExistsError;
                case Persistence, Unspecified -> UpdateStatus.UnspecifiedError;
                default -> UpdateStatus.UnspecifiedError;
            };
        } catch (final Exception exception) {
            log.error(null, exception);
            return UpdateStatus.UnspecifiedError;
        }
    }

    @NotNull
    public DeletionStatus deleteById(@NotNull final TKey id) {
        try {
            return switch (this.getRepository().deleteById(id)) {
                case Success -> DeletionStatus.Success;
                case EntityNotExistsError -> DeletionStatus.EntityNotExistsError;
                case UnspecifiedError -> DeletionStatus.UnspecifiedError;
                default -> DeletionStatus.UnspecifiedError;
            };
        } catch (final Exception exception) {
            log.error(null, exception);
            return DeletionStatus.UnspecifiedError;
        }
    }

    @NotNull
    protected abstract AbstractRepository<TEntity, TDto, TKey> getRepository();
}
