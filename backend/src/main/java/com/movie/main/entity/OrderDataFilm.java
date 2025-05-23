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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
public class OrderDataFilm {
    public static final int VerifyCodeLength = 8;

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

    @Column(nullable = false, length = Film.MaxLengthName)
    @Size(min = Film.MinLengthName, max = Film.MaxLengthName)
    @NotBlank
    private String filmName = "";

    @Column(nullable = false, length = AgeRestriction.MaxLengthName)
    @Size(min = AgeRestriction.MaxLengthName, max = AgeRestriction.MaxLengthName)
    @NotBlank
    private String ageRestriction = "";

    @Column(nullable = false)
    @NotNull
    private LocalDate date = LocalDate.now();

    @Column(nullable = false)
    @NotNull
    private LocalTime time = LocalTime.now();

    @Column(nullable = false, length = VerifyCodeLength)
    @Size(min = VerifyCodeLength, max = VerifyCodeLength)
    @NotBlank
    private String verifyCode = "";

    @Column(nullable = false)
    @NotBlank
    private String roomName = "";

    @ElementCollection(fetch = FetchType.EAGER)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private Set<@NotBlank String> seatNames = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private Set<@NotNull OrderTicket> tickets = new HashSet<>();

    public OrderDataFilm(final CustomerOrder customerOrder, final String filmName, final String ageRestriction,
            final LocalDate date, final LocalTime time, final String verifyCode, final String roomName,
            final Set<@NotBlank String> seatNames, final Set<@NotNull OrderTicket> tickets) {
        this.id = customerOrder.getId();
        this.customerOrder = customerOrder;
        this.filmName = filmName;
        this.ageRestriction = ageRestriction;
        this.date = date;
        this.time = time;
        this.verifyCode = verifyCode;
        this.roomName = roomName;
        this.seatNames = new HashSet<>(seatNames);
        this.tickets = new HashSet<>(tickets);
    }

    public void setCustomerOrder(@NotNull final CustomerOrder customerOrder) {
        this.id = customerOrder.getId();
        this.customerOrder = customerOrder;
    }

    public Set<@NotBlank String> getSeatNames() {
        return new HashSet<>(this.seatNames);
    }

    public void setSeatNames(final Set<@NotBlank String> seatNames) {
        this.seatNames = new HashSet<>(seatNames);
    }

    public Set<@NotNull OrderTicket> getTickets() {
        return new HashSet<>(this.tickets);
    }

    public void setTickets(final Set<@NotNull OrderTicket> tickets) {
        this.tickets = new HashSet<>(tickets);
    }
}
