package com.movie.main.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.experimental.FieldNameConstants;

@Entity
@Table
@FieldNameConstants
public final class User {
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
    private String name = null;

    @Column(nullable = false)
    @NotNull
    private Date birthDate = null;

    @Column(nullable = false)
    @NotBlank
    @Email
    private String email = null;

    @Column(length = MaxLengthPhoneNumber)
    @Size(min = MinLengthPhoneNumber, max = MaxLengthPhoneNumber)
    private String phoneNumber = null;

    @Column(length = MaxLengthUsername, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthUsername, max = MaxLengthUsername)
    private String username = null;

    @Column(nullable = false)
    @NotBlank
    private String hashedPassword = null;

    @Column(nullable = false)
    private boolean blocked = false;

    @Column(nullable = false)
    private boolean deleted = false;
}
