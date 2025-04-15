package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.entity.Tag;

@Repository
public interface TagRepository extends InterfaceRepository<Tag, Integer> {
}
