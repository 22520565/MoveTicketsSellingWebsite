package com.movie.main.entity;

import java.util.Collections;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table
@FieldNameConstants
public final class Theater implements Identifiable<Integer> {
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 100;
    public static final int MinLengthAddress = 1;
    public static final int MaxLengthAddress = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.NONE)
    private Integer id = null;

    @Column(length = MaxLengthName, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = null;

    @Column(length = MaxLengthAddress, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthAddress, max = MaxLengthAddress)
    private String address = null;

    @OneToMany(mappedBy = Room.Fields.theater, cascade = CascadeType.ALL)
    @Setter(value = AccessLevel.NONE)
    private Set<Room> rooms = Collections.emptySet();

    public Theater(final String name, final String address) {
        this.name = name;
        this.address = address;
    }
}
