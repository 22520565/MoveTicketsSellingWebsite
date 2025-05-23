package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.movie.main.dto.internal.CloudinaryImage;
import com.movie.main.dto.request.PromotionRequestDto;
import com.movie.main.entity.Promotion;
import com.movie.main.repository.PromotionRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PromotionService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, CANNOT_DELETE_OLD_THUMBNAIL, UNSPECIFIED,
    }

    public enum UploadThumbnailError {
        ENTITY_NOT_EXISTS, CANNOT_DELETE_OLD, CANNOT_UPLOAD_NEW, UNSPECIFIED,
    }

    public enum MarkPausedStatusResult {
        SUCCESS, ENTITY_NOT_EXISTS_ERROR, UNSPECIFIED_ERROR,
    }

    @NotNull
    private final PromotionRepository repository;

    @NotNull
    private final CloudinaryService cloudinaryService;

    public PromotionService(@NotNull final PromotionRepository repository,
            @NotNull final CloudinaryService cloudinaryService) {
        this.repository = repository;
        this.cloudinaryService = cloudinaryService;
    }

    @NotNull
    public Page<@NotNull Promotion> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public Promotion findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<Promotion, CreationError> create(@NotNull final PromotionRequestDto requestDto) {
        final var newPromotion = new Promotion(requestDto.name(), requestDto.thumbnailUrl(), requestDto.discountRate(),
                requestDto.beginDate(), requestDto.endDate());

        try {
            return Expected.success(this.repository.save(newPromotion));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<Promotion, UpdateError> updateById(final int id, @NotNull final PromotionRequestDto requestDto) {
        final var promotion = this.findById(id);
        if (promotion == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var oldThumbnailUrl = promotion.getThumbnailUrl();
        final var newThumbnailUrl = requestDto.thumbnailUrl();
        if ((oldThumbnailUrl != null) && (oldThumbnailUrl.equals(newThumbnailUrl))) {
            final var oldThumbnailPublicId = promotion.getThumbnailPublicId();

            if ((oldThumbnailPublicId != null) && (!this.cloudinaryService.deleteImage(oldThumbnailPublicId))) {
                return Expected.failure(UpdateError.CANNOT_DELETE_OLD_THUMBNAIL);
            }

            promotion.setThumbnailUrl(newThumbnailUrl);
            promotion.setThumbnailPublicId(null);
        }

        promotion.setName(requestDto.name());
        promotion.setDiscountRate(requestDto.discountRate());
        promotion.setBeginDate(requestDto.beginDate());
        promotion.setEndDate(requestDto.endDate());

        try {
            return Expected.success(this.repository.save(promotion));
        }
        catch (final Exception exception) {
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<CloudinaryImage, UploadThumbnailError> uploadThumbnail(final int id, @NotNull MultipartFile file) {
        final var promotion = this.findById(id);
        if (promotion == null) {
            return Expected.failure(UploadThumbnailError.ENTITY_NOT_EXISTS);
        }

        final var oldThumbnailPublicId = promotion.getThumbnailPublicId();
        if ((oldThumbnailPublicId != null) && (!this.cloudinaryService.deleteImage(oldThumbnailPublicId))) {
            return Expected.failure(UploadThumbnailError.CANNOT_DELETE_OLD);
        }

        final var img = this.cloudinaryService.uploadImage(file);
        if (img == null) {
            return Expected.failure(UploadThumbnailError.CANNOT_UPLOAD_NEW);
        }

        promotion.setThumbnailUrl(img.url());
        promotion.setThumbnailPublicId(img.publicId());

        try {
            this.repository.save(promotion);
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            this.cloudinaryService.deleteImage(img.publicId());
            return Expected.failure(UploadThumbnailError.UNSPECIFIED);
        }

        return Expected.success(img);
    }

    @NotNull
    public MarkPausedStatusResult markAsDeletedById(final int id) {
        return this.markPausedStatusById(id, true);
    }

    @NotNull
    public MarkPausedStatusResult markAsUndeletedById(final int id) {
        return this.markPausedStatusById(id, false);
    }

    @NotNull
    public MarkPausedStatusResult markPausedStatusById(final int id, final boolean pausedStatusToMark) {
        final var film = this.findById(id);
        if (film == null) {
            return MarkPausedStatusResult.ENTITY_NOT_EXISTS_ERROR;
        }

        film.setPaused(pausedStatusToMark);

        try {
            this.repository.save(film);
            return MarkPausedStatusResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return MarkPausedStatusResult.UNSPECIFIED_ERROR;
        }
    }

    @NotNull
    public void deleteById(final int id) {
        this.repository.deleteById(id);
    }
}
