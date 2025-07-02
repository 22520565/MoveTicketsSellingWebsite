package com.movie.main.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class Promotion {
    public static final int MaxLengthThumbnailUrl = 512;
    public static final int MaxLengthThumbnailPublicId = 256;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    private int id = 0;

    @Column(nullable = false)
    @NotBlank
    private String name = "";

    @Column(length = MaxLengthThumbnailUrl)
    private String thumbnailUrl = null;

    @Column(length = MaxLengthThumbnailPublicId)
    private String thumbnailPublicId = null;

    @Column(nullable = false)
    @Min(0)
    private int discountRate = 0;

    @Column(nullable = false)
    private boolean paused = false;

    @Column(nullable = false)
    @NotNull
    private LocalDate beginDate = LocalDate.now();

    @Column(nullable = false)
    @NotNull
    private LocalDate endDate = LocalDate.now();

    public Promotion(
            final String name,
            final String thumbnailUrl,
            final int discountRate,
            final LocalDate beginDate,
            final LocalDate endDate) {
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.discountRate = discountRate;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }
}
