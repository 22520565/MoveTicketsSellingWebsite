package com.movie.main.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class FilmTagId implements Serializable {
    private static final long serialVersionUID = 1;

    private int filmId = 0;
    private int tagId = 0;
}
