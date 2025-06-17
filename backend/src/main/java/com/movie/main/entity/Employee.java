package com.movie.main.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class Employee extends User {
    public enum Permission {
        NORMAL,
        ADMIN,
    }

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

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(value = EnumType.STRING)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private EnumSet<Permission> permissions = EnumSet.noneOf(Employee.Permission.class);

    public Employee(
            final String name,
            final LocalDate birthDate,
            final String email,
            final String phoneNumber,
            final String username,
            final String hashedPassword,
            final String jobTitle,
            final int salary,
            final LocalTime shiftStart,
            final LocalTime shiftEnd,
            final LocalDate beginWorkingDate,
            final Set<Permission> permissions) {
        super(name, birthDate, email, phoneNumber, username, hashedPassword);
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.beginWorkingDate = beginWorkingDate;
        this.permissions = EnumSet.copyOf(permissions);
    }

    public void setPermissions(final Set<Permission> permissions) {
        this.permissions = EnumSet.copyOf(permissions);
    }

    public Set<Permission> getPermissions() {
        return EnumSet.copyOf(permissions);
    }
}
