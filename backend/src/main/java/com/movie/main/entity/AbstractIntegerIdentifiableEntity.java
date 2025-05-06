package com.movie.main.entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractIntegerIdentifiableEntity extends AbstractIdentifiableEntity<Integer> {
    AbstractIntegerIdentifiableEntity() {
        super(0);
    }

    AbstractIntegerIdentifiableEntity(final Integer id) {
        super(id);
    }
}
