package com.movie.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    private int id = 0;

    @Column(nullable = false)
    private int price = 0;

    @Column(nullable = false)
    @NotBlank
    private String thumbnailUrl = "";

    @Column(nullable = false)
    @NotBlank
    private String publicId = "";

    @Column(nullable = false)
    private boolean deleted = false;

    public AdditionalItem(final int price, final String thumbnailUrl, final String publicId) {
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
        this.publicId = publicId;
    }
}
