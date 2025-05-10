package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.movie.main.dto.request.EntityRequestDtoInterface;
import com.movie.main.dto.response.EntityResponseDtoInterface;
import com.movie.main.entity.Identifiable;
import com.movie.main.entity.SoftDeletable;
import com.movie.main.repository.InterfaceSoftDeletableRepository;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractSoftDeletableEntityService<TRequestDto extends EntityRequestDtoInterface,
        TResponseDto extends EntityResponseDtoInterface<TKey>,
        TEntity extends Identifiable<TKey> & SoftDeletable,
        TKey> extends AbstractEntityService<TRequestDto, TResponseDto, TEntity, TKey> {

    @Override
    public TEntity findEntityById(@NotNull final TKey id) {
        return this.getRepository().findByIdAndDeletedFalse(id).orElse(null);
    }

    @Override
    public Page<TEntity> findAllEntities(@NotNull final PageRequest pageRequest) {
        return this.getRepository().findByDeletedFalse(pageRequest);
    }

    @Override
    public DeletionStatus deleteById(@NotNull final TKey id) {
        try {
            final var entity = this.findEntityById(id);
            if (entity == null || entity.isDeleted()) {
                return DeletionStatus.ENTITY_NOT_EXISTS_ERROR;
            }

            entity.setDeleted(true);
            this.save(entity);
            return DeletionStatus.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return DeletionStatus.UNSPECIFIED_ERROR;
        }
    }

    @Override
    protected abstract InterfaceSoftDeletableRepository<TEntity, TKey> getRepository();
}
