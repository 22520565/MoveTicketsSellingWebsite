package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.request.TagRequestDto;
import com.movie.main.dto.response.TagResponseDto;
import com.movie.main.entity.Tag;
import com.movie.main.repository.TagRepository;

import jakarta.validation.constraints.NotNull;

@Service
public class TagService extends AbstractEntityService<TagRequestDto, TagResponseDto, Tag, Integer> {
    @NotNull
    private final TagRepository repository;

    public TagService(@NotNull final TagRepository repository) {
        this.repository = repository;
    }

    @Override
    protected TagResponseDto createResponseDtoFromEntity(@NotNull final Tag tag) {
        return new TagResponseDto(
                tag.getId(),
                tag.getName());
    }

    @Override
    protected Tag createEntityFromRequestDto(@NotNull final TagRequestDto requestDto) {
        return new Tag(requestDto.name());
    }

    @Override
    protected Tag updateEntityFromRequestDto(
            @NotNull final Tag tag,
            @NotNull final TagRequestDto requestDto) {
        tag.setName(requestDto.name());
        return tag;
    }

    @Override
    protected TagRepository getRepository() {
        return this.repository;
    }
}
