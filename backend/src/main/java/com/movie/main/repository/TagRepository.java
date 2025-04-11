package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.dto.TagDto;
import com.movie.main.entity.Tag;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;

@Repository
public class TagRepository extends AbstractRepository<Tag, TagDto, Integer> {
    protected TagRepository(@NotNull final EntityManager entityManager) {
        super(entityManager, Tag.class, TagDto.class);
    }

}
