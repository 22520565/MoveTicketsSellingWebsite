package com.movie.main.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public final class RoomSeat extends IntegerIdentifiableEntity {
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 30;
    public static final int MinLengthType = 1;
    public static final int MaxLengthType = 30;

    @Column(length = MaxLengthName, nullable = false)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = "";

    @Column(nullable = false)
    @NotBlank
    @Size(min = MinLengthType, max = MaxLengthType)
    private String type = "";

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Room room = null;

    public RoomSeat(final String name, final String type, final Room room) {
        this.name = name;
        this.type = type;
        this.room = room;
    }
}
