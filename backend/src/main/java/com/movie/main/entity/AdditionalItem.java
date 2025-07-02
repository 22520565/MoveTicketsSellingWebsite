package com.movie.main.entity;

import org.hibernate.validator.constraints.Range;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class AdditionalItem {
    public static final int MaxLengthName = 50;
    public static final int MaxLengthThumbnailUrl = 512;
    public static final int MaxLengthThumbnailPublicId = 256;
    public static final int MinLoyalPointRate = 0;
    public static final int MaxLoyalPointRate = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    private int id = 0;

    @Column(nullable = false, length = MaxLengthName)
    @NotBlank
    @Size(max = MaxLengthName)
    private String name = "";

    @Column(nullable = false)
    @Min(0)
    private int price = 0;

    @Column(nullable = false)
    @Range(min = MinLoyalPointRate, max = MaxLoyalPointRate)
    private int loyalPointRate = 0;

    @Column(length = MaxLengthThumbnailUrl)
    @Nullable
    private String thumbnailUrl = null;

    @Column(length = MaxLengthThumbnailPublicId)
    @Nullable
    private String thumbnailPublicId = null;

    @Column(nullable = false)
    private boolean deleted = false;

    public AdditionalItem(
            final String name,
            final int price,
            final int loyalPointRate,
            final String thumbnailUrl) {
        this.name = name;
        this.price = price;
        this.loyalPointRate = loyalPointRate;
        this.thumbnailUrl = thumbnailUrl;
    }
}
