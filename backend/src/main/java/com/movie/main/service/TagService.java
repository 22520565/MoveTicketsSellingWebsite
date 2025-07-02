package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.TagRequestDto;
import com.movie.main.entity.Tag;
import com.movie.main.repository.TagRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TagService {
    public enum CreationError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    @NotNull
    private final TagRepository repository;

    public TagService(@NotNull final TagRepository repository) {
        this.repository = repository;
    }

    @NotNull
    public Page<@NotNull Tag> findAll(
            @NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public Tag findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<Tag, CreationError> create(
            @NotNull final TagRequestDto requestDto) {
        final var newTag = new Tag(requestDto.name());

        try {
            return Expected.success(this.repository.save(newTag));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<Tag, UpdateError> updateById(
            final int id,
            @NotNull final TagRequestDto requestDto) {
        final var tag = this.findById(id);
        if (tag == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        tag.setName(requestDto.name());

        try {
            return Expected.success(this.repository.save(tag));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public void deleteById(final int id) {
        this.repository.deleteById(id);
    }
}
