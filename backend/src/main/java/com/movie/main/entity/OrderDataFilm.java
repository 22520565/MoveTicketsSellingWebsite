package com.movie.main.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.validation.constraints.NotNull;
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
public class OrderDataFilm extends IntegerIdentifiableEntity {
    @MapsId
    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @NotNull
    private CustomerOrder customerOrder = null;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private FilmShow filmShow = null;

    public OrderDataFilm(final CustomerOrder customerOrder, final FilmShow filmShow) {
        this.customerOrder = customerOrder;
        this.filmShow = filmShow;
    }
}
