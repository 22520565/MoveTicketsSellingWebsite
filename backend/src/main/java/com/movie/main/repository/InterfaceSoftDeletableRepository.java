package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.movie.main.entity.SoftDeletable;

import jakarta.validation.constraints.NotNull;

public interface InterfaceSoftDeletableRepository<TEntity extends SoftDeletable, TKey>
        extends InterfaceRepository<TEntity, TKey> {
    Page<TEntity> findByDeletedFalse(@NotNull final Pageable pageable);

    Optional<TEntity> findByIdAndDeletedFalse(final TKey id);
}
