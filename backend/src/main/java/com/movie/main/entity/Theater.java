package com.movie.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
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
public final class Theater extends AbstractIntegerIdentifiableEntity {
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 100;
    public static final int MinLengthAddress = 1;
    public static final int MaxLengthAddress = 200;

    @Column(length = MaxLengthName, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = "";

    @Column(length = MaxLengthAddress, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthAddress, max = MaxLengthAddress)
    private String address = "";

    public Theater(final String name, final String address) {
        this.name = name;
        this.address = address;
    }
}
