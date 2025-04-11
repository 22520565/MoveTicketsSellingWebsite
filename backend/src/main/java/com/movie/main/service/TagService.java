package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.TagRequestDto;
import com.movie.main.entity.Tag;
import com.movie.main.repository.TagRepository;
import com.movie.main.ulti.Expected;

import jakarta.validation.constraints.NotNull;

@Service
public class TagService extends AbstractService<TagRequestDto, Tag, Integer> {
    @NotNull
    private final TagRepository repository;

    public TagService(@NotNull final TagRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tag create(@NotNull final TagRequestDto requestDto) {
        final var newTag = new Tag(requestDto.name());
        return this.save(newTag);
    }

    @Override
    public Expected<Tag, UpdateError> update(@NotNull final Integer id, @NotNull final TagRequestDto requestDto) {
        var tag = this.findById(id);
        if (tag == null) {
            return Expected.failure(UpdateError.EntityNotExists);
        }

        tag.setName(requestDto.name());

        tag = this.save(tag);
        if (tag == null) {
            return Expected.failure(UpdateError.EntityNotExists);
        }

        return Expected.success(tag);
    }

    @Override
    protected TagRepository getRepository() {
        return this.repository;
    }
}
