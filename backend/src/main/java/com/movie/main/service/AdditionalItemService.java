package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.movie.main.dto.internal.CloudinaryImage;
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
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS,
        CANNOT_DELETE_OLD_THUMBNAIL,
        UNSPECIFIED,
    }

    public enum UploadThumbnailError {
        ENTITY_NOT_EXISTS,
        CANNOT_DELETE_OLD,
        CANNOT_UPLOAD_NEW,
        UNSPECIFIED,
    }

    public enum MarkDeletedStatusResult {
        SUCCESS,
        ENTITY_NOT_EXISTS_ERROR,
        UNSPECIFIED_ERROR,
    }

    @NotNull
    private final AdditionalItemRepository repository;

    @NotNull
    private final CloudinaryService cloudinaryService;

    public AdditionalItemService(@NotNull final AdditionalItemRepository repository,
            @NotNull final CloudinaryService cloudinaryService) {
        this.repository = repository;
        this.cloudinaryService = cloudinaryService;
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
        final var newAdditionalItem = new AdditionalItem(
                requestDto.name(),
                requestDto.price(),
                requestDto.thumbnailUrl());

        try {
            return Expected.success(this.repository.save(newAdditionalItem));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<AdditionalItem, UpdateError> updateByIdAndDeletedFalse(
            final int id,
            @NotNull final AdditionalItemRequestDto requestDto) {
        final var additionalItem = this.findByIdAndDeletedFalse(id);
        if (additionalItem == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var oldThumbnailUrl = additionalItem.getThumbnailUrl();
        final var newThumbnailUrl = requestDto.thumbnailUrl();
        if ((oldThumbnailUrl != null) && (oldThumbnailUrl.equals(newThumbnailUrl))) {
            final var oldThumbnailPublicId = additionalItem.getThumbnailPublicId();

            if ((oldThumbnailPublicId != null) && (!this.cloudinaryService.deleteImage(oldThumbnailPublicId))) {
                log.error(UpdateError.CANNOT_DELETE_OLD_THUMBNAIL.name());
                return Expected.failure(UpdateError.CANNOT_DELETE_OLD_THUMBNAIL);
            }

            additionalItem.setThumbnailUrl(newThumbnailUrl);
            additionalItem.setThumbnailPublicId(null);
        }

        additionalItem.setName(requestDto.name());
        additionalItem.setPrice(requestDto.price());

        try {
            return Expected.success(this.repository.save(additionalItem));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<CloudinaryImage, UploadThumbnailError> uploadThumbnail(final int id, @NotNull MultipartFile file) {
        final var additionalItem = this.findByIdAndDeletedFalse(id);
        if (additionalItem == null) {
            return Expected.failure(UploadThumbnailError.ENTITY_NOT_EXISTS);
        }

        final var oldThumbnailPublicId = additionalItem.getThumbnailPublicId();
        if ((oldThumbnailPublicId != null) && (!this.cloudinaryService.deleteImage(oldThumbnailPublicId))) {
            return Expected.failure(UploadThumbnailError.CANNOT_DELETE_OLD);
        }

        final var img = this.cloudinaryService.uploadImage(file);
        if (img == null) {
            return Expected.failure(UploadThumbnailError.CANNOT_UPLOAD_NEW);
        }

        additionalItem.setThumbnailUrl(img.url());
        additionalItem.setThumbnailPublicId(img.publicId());

        try {
            this.repository.save(additionalItem);
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            this.cloudinaryService.deleteImage(img.publicId());
            return Expected.failure(UploadThumbnailError.UNSPECIFIED);
        }

        return Expected.success(img);
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
