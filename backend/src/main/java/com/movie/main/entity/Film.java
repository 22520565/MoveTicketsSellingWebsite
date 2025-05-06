package com.movie.main.entity;

import java.sql.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public final class Film extends AbstractIntegerIdentifiableEntity {
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 100;
    public static final int MinLengthDescription = 1;
    public static final int MaxLengthDescription = 1000;

    @Column(length = MaxLengthName, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = "";

    @Column(nullable = false, unique = true)
    @NotBlank
    private String thumbnailUrl = "";

    @Column(nullable = false, unique = true)
    @NotBlank
    private String trailerUrl = "";

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Tag tag = null;

    @Column(nullable = false)
    @NotNull
    @Min(1)
    private int duration = 0;

    @Column(nullable = false)
    @NotBlank
    private String ageRestriction = "";

    @Column(nullable = false)
    @NotBlank
    private String voice = "";

    @Column(nullable = false)
    @NotBlank
    private String originatedCountry = "";

    @Column(nullable = false)
    private boolean is3D = false;

    @Column(nullable = false, length = MaxLengthDescription)
    @NotBlank
    @Size(min = MinLengthDescription, max = MaxLengthDescription)
    private String description = "";

    @Column(nullable = false)
    @NotBlank
    private String content = "";

    @Column(nullable = false)
    @NotNull
    private Date beginDate = new Date(0);

    @Column(nullable = false)
    private boolean deleted = false;

    public Film(final String name, final String thumbnailUrl, final String trailerUrl, final Tag tag,
            final int duration, final String ageRestriction, final String voice, final String originatedCountry,
            final boolean is3D, final String content, final Date beginDate) {
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.trailerUrl = trailerUrl;
        this.tag = tag;
        this.duration = duration;
        this.ageRestriction = ageRestriction;
        this.voice = voice;
        this.originatedCountry = originatedCountry;
        this.is3D = is3D;
        this.content = content;
        this.beginDate = new Date(beginDate.getTime());
    }
}
