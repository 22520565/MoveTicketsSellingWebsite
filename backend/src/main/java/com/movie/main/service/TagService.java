package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.TagDto;
import com.movie.main.entity.Tag;
import com.movie.main.repository.TagRepository;
import com.movie.main.service.enumclass.UpdateStatus;

import jakarta.validation.constraints.NotNull;

@Service
public class TagService extends AbstractService<Tag, TagDto, Integer> {
    @NotNull
    private final TagRepository repository;

    public TagService(@NotNull final TagRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tag create(@NotNull final TagDto dto) {
        final var newTag = new Tag(dto);
        return this.create(newTag);
    }

    @Override
    public UpdateStatus update(@NotNull final Integer id, @NotNull final TagDto dto) {
        final var tag = this.repository.findById(id);
        if (tag == null) {
            return UpdateStatus.EntityNotExistsError;
        }

        tag.updateFromDto(dto);
        return this.update(tag);
    }

    @Override
    protected TagRepository getRepository() {
        return this.repository;
    }
}
