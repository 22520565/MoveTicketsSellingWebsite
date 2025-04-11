package com.movie.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;

@NoRepositoryBean
public interface InterfaceRepository<TEntity, TKey> extends JpaRepository<TEntity, TKey> {
    @NonNull
    Page<TEntity> findAll(@NonNull final Pageable pageable);
}
