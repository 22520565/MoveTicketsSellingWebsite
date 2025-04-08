package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.movie.main.dto.InterfaceDTO;
import com.movie.main.entity.Identifiable;
import com.movie.main.repository.AbstractRepository;
import com.movie.main.service.enumclass.DeletionStatus;
import com.movie.main.service.enumclass.UpdateStatus;

import io.micrometer.common.lang.Nullable;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractService<TEntity extends Identifiable<TKey>, TDto extends InterfaceDTO, TKey> {
    @Nonnull
    protected abstract AbstractRepository<TEntity, TDto, TKey> getRepository();

    @Nonnull
    public Page<TDto> findAllData(@Nonnull final PageRequest pageRequest) {
        return this.getRepository().findAllData(pageRequest);
    }

    @Nullable
    public TEntity findById(@Nonnull final TKey id) {
        return this.getRepository().findById(id);
    }

    @Nullable
    public TDto findDataById(@Nonnull final TKey id) {
        return this.getRepository().findDataById(id);
    }

    @Nullable
    public abstract TEntity create(@Nonnull final TDto dto);

    @Nullable
    public TEntity create(@Nonnull final TEntity entity) {
        try {
            final var result = this.getRepository().add(entity);

            return result.getValue();
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    @Nullable
    public abstract UpdateStatus update(@Nonnull final TKey id, @Nonnull final TDto dto);

    @Nonnull
    public UpdateStatus update(@Nonnull final TEntity entity) {
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

    @Nonnull
    public DeletionStatus deleteById(@Nonnull final TKey id) {
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
}
