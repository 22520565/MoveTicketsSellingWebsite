package com.movie.main.entity;

import java.sql.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    private Integer id = 0;

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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
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

    public Film(
            final String name,
            final String thumbnailUrl,
            final String trailerUrl,
            final Tag tag,
            final int duration,
            final String ageRestriction,
            final String voice,
            final String originatedCountry,
            final boolean is3D,
            final String content,
            final Date beginDate) {
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
