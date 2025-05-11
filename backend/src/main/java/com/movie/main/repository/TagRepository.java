package com.movie.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Tag;

import jakarta.validation.constraints.NotNull;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    @NonNull
    Page<@NotNull Tag> findAll(@NonNull final Pageable pageable);
}
