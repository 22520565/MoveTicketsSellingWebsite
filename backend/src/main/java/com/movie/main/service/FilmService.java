package com.movie.main.service;

import com.movie.main.dto.internal.CloudinaryImage;
import com.movie.main.dto.request.FilmRequestDto;
import com.movie.main.entity.Film;
import com.movie.main.entity.Tag;
import com.movie.main.repository.FilmRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FilmService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, CANNOT_DELETE_OLD_THUMBNAIL, UNSPECIFIED,
    }

    public enum UploadThumbnailError {
        ENTITY_NOT_EXISTS, CANNOT_DELETE_OLD, CANNOT_UPLOAD_NEW, UNSPECIFIED,
    }

    public enum MarkDeletedStatusResult {
        SUCCESS, ENTITY_NOT_EXISTS_ERROR, UNSPECIFIED_ERROR,
    }

    @NotNull
    private final FilmRepository repository;

    @NotNull
    private final TagService tagService;

    @NotNull
    private final CloudinaryService cloudinaryService;

    protected FilmService(@NotNull final FilmRepository repository, @NotNull final TagService tagService,
            @NotNull final CloudinaryService cloudinaryService) {
        this.repository = repository;
        this.tagService = tagService;
        this.cloudinaryService = cloudinaryService;
    }

    @NotNull
    public Page<@NotNull Film> findAllByDeletedFalse(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedFalse(pageRequest);
    }

    @NotNull
    public Page<@NotNull Film> findAllByDeletedTrue(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedTrue(pageRequest);
    }

    @NotNull
    public Page<@NotNull Film> searchAllFilmsWithTagsByDeletedFalse(String keyword,
            @NotNull final PageRequest pageRequest) {
        return this.repository.searchAllFilmsWithTagsByDeletedFalse(keyword, pageRequest);
    }

    @NotNull
    public Page<@NotNull Film> findAllByTheaterIdAndDeletedFalseOrderByShowDate(final int theaterId,
            @NotNull final PageRequest pageRequest) {
        return this.repository.findAllByTheaterIdAndDeletedFalseOrderByShowDate(theaterId, pageRequest);
    }

    @Nullable
    public Film findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @Nullable
    public Film findByIdAndDeletedFalse(final int id) {
        return this.repository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Nullable
    public Film findByIdAndDeletedTrue(final int id) {
        return this.repository.findByIdAndDeletedTrue(id).orElse(null);
    }

    @NotNull
    public Expected<Film, CreationError> create(@NotNull final FilmRequestDto requestDto) {
        final var tagsId = requestDto.tagIds();
        final HashSet<@NotNull Tag> tags = HashSet.newHashSet(tagsId.size());
        for (final var tagId : requestDto.tagIds()) {
            final var tag = this.tagService.findById(tagId);

            if (tag == null) {
                return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
            }

            tags.add(tag);
        }

        final var newFilm = new Film(requestDto.name(), requestDto.thumbnailUrl(), requestDto.trailerUrl(), tags,
                requestDto.duration(), requestDto.ageRestriction(), requestDto.voice(), requestDto.originatedCountry(),
                requestDto.is3D(), requestDto.description(), requestDto.content(), requestDto.beginDate());

        try {
            return Expected.success(this.repository.save(newFilm));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<Film, UpdateError> updateByIdAndDeletedFalse(final int id,
            @NotNull final FilmRequestDto requestDto) {
        final var film = this.findByIdAndDeletedFalse(id);
        if (film == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var tagsId = requestDto.tagIds();
        final HashSet<@NotNull Tag> tags = HashSet.newHashSet(tagsId.size());
        for (final var tagId : requestDto.tagIds()) {
            final var tag = this.tagService.findById(tagId);

            if (tag == null) {
                return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
            }

            tags.add(tag);
        }

        final var oldThumbnailUrl = film.getThumbnailUrl();
        final var newThumbnailUrl = requestDto.thumbnailUrl();
        if ((oldThumbnailUrl != null) && (oldThumbnailUrl.equals(newThumbnailUrl))) {
            final var oldThumbnailPublicId = film.getThumbnailPublicId();

            if ((oldThumbnailPublicId != null) && (!this.cloudinaryService.deleteImage(oldThumbnailPublicId))) {
                return Expected.failure(UpdateError.CANNOT_DELETE_OLD_THUMBNAIL);
            }

            film.setThumbnailUrl(newThumbnailUrl);
            film.setThumbnailPublicId(null);
        }

        film.setName(requestDto.name());
        film.setTrailerUrl(requestDto.trailerUrl());
        film.setTags(tags);
        film.setDuration(requestDto.duration());
        film.setAgeRestriction(requestDto.ageRestriction());
        film.setVoice(requestDto.voice());
        film.setOriginatedCountry(requestDto.originatedCountry());
        film.set3D(requestDto.is3D());
        film.setDescription(requestDto.description());
        film.setContent(requestDto.content());
        film.setBeginDate(requestDto.beginDate());

        try {
            return Expected.success(this.repository.save(film));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<CloudinaryImage, UploadThumbnailError> uploadThumbnail(final int id, @NotNull MultipartFile file) {
        final var film = this.findByIdAndDeletedFalse(id);
        if (film == null) {
            return Expected.failure(UploadThumbnailError.ENTITY_NOT_EXISTS);
        }

        final var oldThumbnailPublicId = film.getThumbnailPublicId();
        if ((oldThumbnailPublicId != null) && (!this.cloudinaryService.deleteImage(oldThumbnailPublicId))) {
            return Expected.failure(UploadThumbnailError.CANNOT_DELETE_OLD);
        }

        final var img = this.cloudinaryService.uploadImage(file);
        if (img == null) {
            return Expected.failure(UploadThumbnailError.CANNOT_UPLOAD_NEW);
        }

        film.setThumbnailUrl(img.url());
        film.setThumbnailPublicId(img.publicId());

        try {
            this.repository.save(film);
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
        final var film = this.findById(id);
        if (film == null) {
            return MarkDeletedStatusResult.ENTITY_NOT_EXISTS_ERROR;
        }

        film.setDeleted(deletedStatusToMark);

        try {
            this.repository.save(film);
            return MarkDeletedStatusResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return MarkDeletedStatusResult.UNSPECIFIED_ERROR;
        }
    }
}
