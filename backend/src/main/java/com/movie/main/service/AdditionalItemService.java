package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.AdditionalItemRequestDto;
import com.movie.main.entity.AdditionalItem;
import com.movie.main.repository.AdditionalItemRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdditionalItemService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum MarkDeletedStatusResult {
        SUCCESS, ENTITY_NOT_EXISTS_ERROR, UNSPECIFIED_ERROR,
    }

    @NotNull
    private final AdditionalItemRepository repository;

    public AdditionalItemService(@NotNull final AdditionalItemRepository repository) {
        this.repository = repository;
    }

    @NotNull
    public Page<@NotNull AdditionalItem> findAllByDeletedFalse(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedFalse(pageRequest);
    }

    @NotNull
    public Page<@NotNull AdditionalItem> findAllByDeletedTrue(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedTrue(pageRequest);
    }

    @Nullable
    public AdditionalItem findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @Nullable
    public AdditionalItem findByIdAndDeletedFalse(final int id) {
        return this.repository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Nullable
    public AdditionalItem findByIdAndDeletedTrue(final int id) {
        return this.repository.findByIdAndDeletedTrue(id).orElse(null);
    }

    @NotNull
    public Expected<AdditionalItem, CreationError> create(@NotNull final AdditionalItemRequestDto requestDto) {
        final var newAdditionalItem = new AdditionalItem(requestDto.price(), requestDto.thumbnailUrl(),
                requestDto.publicId());

        try {
            return Expected.success(this.repository.save(newAdditionalItem));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<AdditionalItem, UpdateError> updateByIdAndDeletedFalse(final int id,
            @NotNull final AdditionalItemRequestDto requestDto) {
        final var additionalItem = this.findByIdAndDeletedFalse(id);
        if (additionalItem == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        additionalItem.setPrice(requestDto.price());
        additionalItem.setThumbnailUrl(requestDto.thumbnailUrl());
        additionalItem.setPublicId(requestDto.publicId());

        try {
            return Expected.success(this.repository.save(additionalItem));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public MarkDeletedStatusResult markAsDeletedById(final int id) {
        return this.markDeletedStatusById(id, true);
    }

    @NotNull
    public MarkDeletedStatusResult markAsUndeletedById(final int id) {
        return this.markDeletedStatusById(id, false);
    }

    @NotNull
    public MarkDeletedStatusResult markDeletedStatusById(final int id, final boolean deletedStatusToMark) {
        final var additionalItem = this.findById(id);
        if (additionalItem == null) {
            return MarkDeletedStatusResult.ENTITY_NOT_EXISTS_ERROR;
        }

        additionalItem.setDeleted(deletedStatusToMark);

        try {
            this.repository.save(additionalItem);
            return MarkDeletedStatusResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return MarkDeletedStatusResult.UNSPECIFIED_ERROR;
        }
    }
}
