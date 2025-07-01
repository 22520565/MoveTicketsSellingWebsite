package com.movie.main.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class OrderDataFilm {
    @Id
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    private int id = 0;

    @MapsId
    @OneToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @Setter(value = AccessLevel.NONE)
    @NotNull
    private CustomerOrder customerOrder = null;

    @Column(nullable = false)
    @NotNull
    private LocalDate date = LocalDate.now();

    @Column(nullable = false)
    @NotNull
    private LocalTime time = LocalTime.now();

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private FilmShow filmShow = null;

    @ElementCollection(fetch = FetchType.EAGER)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private Set<@NotBlank RoomSeat> roomSeats = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private Set<@NotNull OrderTicket> orderTickets = new HashSet<>();

    public OrderDataFilm(
            final CustomerOrder customerOrder,
            final LocalDate date,
            final LocalTime time,
            final FilmShow filmShow,
            final Set<@NotBlank RoomSeat> roomSeats,
            final Set<@NotNull OrderTicket> orderTickets) {
        this.customerOrder = customerOrder;
        this.date = date;
        this.time = time;
        this.filmShow = filmShow;
        this.roomSeats = new HashSet<>(roomSeats);
        this.orderTickets = new HashSet<>(orderTickets);
    }

    public void setCustomerOrder(@NotNull final CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public Set<@NotBlank RoomSeat> getRoomSeats() {
        return new HashSet<>(this.roomSeats);
    }

    public void setSeatNames(final Set<@NotBlank RoomSeat> roomSeats) {
        this.roomSeats = new HashSet<>(roomSeats);
    }

    public Set<@NotNull OrderTicket> getOrderTickets() {
        return new HashSet<>(this.orderTickets);
    }

    public void setOrderTickets(final Set<@NotNull OrderTicket> orderTickets) {
        this.orderTickets = new HashSet<>(orderTickets);
    }
}
