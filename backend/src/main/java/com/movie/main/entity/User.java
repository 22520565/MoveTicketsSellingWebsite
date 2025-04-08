package com.movie.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.experimental.FieldNameConstants;

@Entity
@Table
@FieldNameConstants
public final class User {
    public static final String DefaultUsername = "DefaultUsername";
    public static final int MinLengthUsername = 1;
    public static final int MaxLengthUsername = 30;
    public static final int MinLengthPhoneNumber = 1;
    public static final int MaxLengthPhoneNumber = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    private Integer id = 0;

    @Column(length = MaxLengthUsername, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthUsername, max = MaxLengthUsername)
    private String username = DefaultUsername;

    @Column(length = MaxLengthPhoneNumber)
    @Size(min = MinLengthPhoneNumber, max = MaxLengthPhoneNumber)
    private String phoneNumber = null;

    public User(final int id, final String username, final String phoneNumber) {
        this.id = id;

        if ((username != null) && (!username.isBlank())) {
            this.username = username;
        } else {
            this.username = DefaultUsername;
        }

        this.phoneNumber = phoneNumber;
    }

    public Integer getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean setUsername(final String newUsername) {
        if ((newUsername == null) || (newUsername.isBlank())) {
            return false;
        }

        final var usernameLength = newUsername.length();
        if ((usernameLength < MaxLengthUsername) || (usernameLength > MaxLengthUsername)) {
            return false;
        }

        this.username = newUsername;
        return true;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public boolean setPhoneNumber(final String newPhoneNumber) {
        if ((newPhoneNumber == null) || (newPhoneNumber.isBlank())) {
            this.phoneNumber = null;
            return true;
        }

        final var phoneNumberLength = newPhoneNumber.length();
        if ((phoneNumberLength < MinLengthPhoneNumber) || (phoneNumberLength > MaxLengthPhoneNumber)) {
            return false;
        }

        this.phoneNumber = newPhoneNumber;
        return true;
    }
}
