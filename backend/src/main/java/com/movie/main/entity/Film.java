package com.movie.main.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class Film {
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 100;
    public static final int MaxAmountTags = 5;
    public static final int MaxLengthThumbnailUrl = 512;
    public static final int MaxLengthThumbnailPublicId = 256;
    public static final int MinLengthDescription = 1;
    public static final int MaxLengthDescription = 1000;
    public static final int MinLengthContent = 1;
    public static final int MaxLengthContent = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    private int id = 0;

    @Column(length = MaxLengthName, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = "";

    @Column(length = MaxLengthThumbnailUrl)
    @Nullable
    private String thumbnailUrl = null;

    @Column(length = MaxLengthThumbnailPublicId)
    @Nullable
    private String thumbnailPublicId = null;

    private String trailerUrl = "";

    @ManyToMany(fetch = FetchType.EAGER)
    @Size(max = MaxAmountTags)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private Set<@NotNull Tag> tags = new HashSet<>();

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

    @Column(nullable = false, length = MaxLengthContent)
    @NotBlank
    @Size(min = MinLengthContent, max = MaxLengthContent)
    private String content = "";

    @Column(nullable = false)
    @NotNull
    private LocalDate beginDate = LocalDate.now();

    @Column(nullable = false)
    private boolean deleted = false;

    public Film(
            final String name,
            final String thumbnailUrl,
            final String trailerUrl,
            final Set<@NotNull Tag> tags,
            final int duration,
            final String ageRestriction,
            final String voice,
            final String originatedCountry,
            final boolean is3D,
            final String description,
            final String content,
            final LocalDate beginDate) {
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.trailerUrl = trailerUrl;
        this.tags = new HashSet<>(tags);
        this.duration = duration;
        this.ageRestriction = ageRestriction;
        this.voice = voice;
        this.originatedCountry = originatedCountry;
        this.is3D = is3D;
        this.description = description;
        this.content = content;
        this.beginDate = beginDate;
    }

    public void setTags(final Set<@NotNull Tag> tags) {
        this.tags = new HashSet<>(tags);
    }

    public Set<@NotNull Tag> getTags() {
        return new HashSet<>(this.tags);
    }
}
