package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.request.TagRequestDto;
import com.movie.main.dto.response.TagResponseDto;
import com.movie.main.entity.Tag;
import com.movie.main.repository.TagRepository;

import jakarta.validation.constraints.NotNull;

@Service
public class TagService extends AbstractService<TagRequestDto, TagResponseDto, Tag, Integer> {
    @NotNull
    private final TagRepository repository;

    public TagService(@NotNull final TagRepository repository) {
        this.repository = repository;
    }

    @Override
    protected TagResponseDto createResponseDtoFromEntity(@NotNull final Tag entity) {
        return new TagResponseDto(
                entity.getId(),
                entity.getName());
    }

    @Override
    protected Tag createEntityFromRequestDto(@NotNull final TagRequestDto requestDto) {
        return new Tag(requestDto.name());
    }

    @Override
    protected Tag updateEntityFromRequestDto(@NotNull final Tag entity, @NotNull final TagRequestDto requestDto) {
        entity.setName(requestDto.name());
        return entity;
    }

    @Override
    protected TagRepository getRepository() {
        return this.repository;
    }
}
