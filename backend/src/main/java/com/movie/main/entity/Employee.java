package com.movie.main.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Table
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants
public class Employee implements UserDetailsInterface {
    public enum Permission {
        Normal,
        Admin,
    }

    @MapsId
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @NotNull
    private User user = null;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.NONE)
    private Integer id = 0;

    @Column(nullable = false)
    @NotNull
    private String jobTitle = "";

    @Column(nullable = false)
    @Min(0)
    private int salary = 0;

    @Column(nullable = false)
    private LocalTime shiftStart = LocalTime.now();

    @Column(nullable = false)
    private LocalTime shiftEnd = LocalTime.now();

    @Column(nullable = false)
    private LocalDate beginWorkingDate = LocalDate.now();

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(value = EnumType.STRING)
    private Set<Permission> permissions = Collections.emptySet();

    public Employee(
            final User user,
            final String jobTitle,
            final int salary,
            final LocalTime shiftStart,
            final LocalTime shiftEnd,
            final LocalDate beginWorkingDate) {
        this.user = user;
        this.id = user.getId();
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.beginWorkingDate = beginWorkingDate;
    }
}
