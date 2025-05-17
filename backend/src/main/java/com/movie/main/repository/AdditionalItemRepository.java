package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.movie.main.entity.AdditionalItem;

import jakarta.validation.constraints.NotNull;

public interface AdditionalItemRepository extends JpaRepository<AdditionalItem, Integer> {
    @NotNull
    Optional<AdditionalItem> findByIdAndDeletedFalse(final int id);

    @NotNull
    Optional<AdditionalItem> findByIdAndDeletedTrue(final int id);

    @NonNull
    Page<@NotNull AdditionalItem> findAllByDeletedFalse(@NonNull final Pageable pageable);

    @NonNull
    Page<@NotNull AdditionalItem> findAllByDeletedTrue(@NonNull final Pageable pageable);
}
