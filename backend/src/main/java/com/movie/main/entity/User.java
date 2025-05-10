package com.movie.main.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
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
public final class User extends IntegerIdentifiableEntity implements SoftDeletable {
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 50;
    public static final int MinLengthUsername = 1;
    public static final int MaxLengthUsername = 30;
    public static final int MinLengthPhoneNumber = 1;
    public static final int MaxLengthPhoneNumber = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    private Integer id = 0;

    @Column(length = MaxLengthName, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = "";

    @Column(nullable = false)
    @NotNull
    private LocalDate birthDate = LocalDate.now();

    @Column(nullable = false)
    @NotBlank
    @Email
    private String email = "";

    @Column(length = MaxLengthPhoneNumber)
    @Size(min = MinLengthPhoneNumber, max = MaxLengthPhoneNumber)
    private String phoneNumber = "";

    @Column(length = MaxLengthUsername, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthUsername, max = MaxLengthUsername)
    private String username = "";

    @Column(nullable = false)
    @NotBlank
    private String hashedPassword = "";

    @Column(nullable = false)
    private boolean blocked = false;

    @Column(nullable = false)
    private boolean deleted = false;

    public User(final String name, final LocalDate birthDate, final String email, final String phoneNumber,
            final String username, final String hashedPassword) {
        this.name = name;
        this.birthDate = birthDate;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.hashedPassword = hashedPassword;
    }
}
