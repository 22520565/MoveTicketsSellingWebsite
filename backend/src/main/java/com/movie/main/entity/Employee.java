package com.movie.main.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
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
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class Employee extends AbstractUserDetail {
    public static final UserRole userRole = UserRole.EMPLOYEE;

    public enum Permission {
        NORMAL, ADMIN,
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
    private Set<Permission> permissions = Collections.emptySet();

    public Employee(final User user, final String jobTitle, final int salary, final LocalTime shiftStart,
            final LocalTime shiftEnd, final LocalDate beginWorkingDate, final Set<Permission> permissions) {
        super(user);
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.beginWorkingDate = beginWorkingDate;
        this.permissions = Collections.unmodifiableSet(permissions);
    }

    public void setPermissions(final Set<Permission> permissions) {
        this.permissions = Collections.unmodifiableSet(permissions);
    }

    public Set<Permission> getPermissions() {
        return new HashSet<>(this.permissions);
    }
}
