package com.movie.main.entity;

import java.sql.Date;

import com.movie.main.dto.FilmDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Table
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants
public final class Film implements Identifiable<Integer> {
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 100;
    public static final int MinLengthDescription = 1;
    public static final int MaxLengthDescription = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.NONE)
    private final Integer id = null;

    @Column(length = MaxLengthName, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = null;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String thumbnailUrl = null;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String trailerUrl = null;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Tag tag = null;

    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer duration = null;

    @Column(nullable = false)
    @NotBlank
    private String ageRestriction = null;

    @Column(nullable = false)
    @NotBlank
    private String voice = null;

    @Column(nullable = false)
    @NotBlank
    private String originatedCountry = null;

    @Column(nullable = false)
    private boolean is3D = false;

    @Column(nullable = false, length = MaxLengthDescription)
    @NotBlank
    @Size(min = MinLengthDescription, max = MaxLengthDescription)
    private String description = null;

    @Column(nullable = false)
    @NotBlank
    private String content = null;

    @Column(nullable = false)
    @NotNull
    private Date beginDate = null;

    @Column(nullable = false)
    private boolean deleted = false;

    public Film(@NotNull final FilmDto dto, @NotNull final Tag tag) {
        this.updateFromDto(dto, tag);
    }

    public void updateFromDto(@NotNull final FilmDto dto, @NotNull final Tag tag) {
        this.name = dto.name();
        this.thumbnailUrl = dto.thumbnailUrl();
        this.trailerUrl = dto.trailerUrl();
        this.tag = tag;
        this.duration = dto.duration();
        this.ageRestriction = dto.ageRestriction();
        this.voice = dto.voice();
        this.originatedCountry = dto.originatedCountry();
        this.is3D = dto.is3D();
        this.description = dto.description();
        this.content = dto.content();
        this.beginDate = dto.beginDate();
    }
}
