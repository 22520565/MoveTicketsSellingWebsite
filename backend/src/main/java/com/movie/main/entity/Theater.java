package com.movie.main.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table
@FieldNameConstants
@Slf4j
public final class Theater {
    public static final String DefaultName = "Default Name";
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 100;
    public static final String DefaultAddress = "Default Address";
    public static final int MinLengthAddress = 1;
    public static final int MaxLengthAddress = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    private Integer id = 0;

    @Column(length = MaxLengthName, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = DefaultName;

    @Column(length = MaxLengthAddress, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthAddress, max = MaxLengthAddress)
    private String address = DefaultAddress;

    @OneToMany(mappedBy = Room.Fields.theater, cascade = CascadeType.ALL)
    private Set<Room> rooms = Collections.unmodifiableSet(new HashSet<>());

    protected Theater() {
    }

    private Theater(final String name, final String address, final AtomicBoolean succeed) {
        succeed.set(this.setName(name) && this.setAddress(address));
    }

    @Nullable
    public static Theater create(final String name, final String address) {
        try {
            final var succeed = new AtomicBoolean(false);
            final var newTheater = new Theater(name, address, succeed);

            if (succeed.get()) {
                return newTheater;
            } else {
                return null;
            }
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean setName(final String newName) {
        if ((newName == null) || (newName.isBlank())) {
            return false;
        }

        var newNameLength = newName.length();
        if ((newNameLength < MinLengthName) || (newNameLength > MaxLengthName)) {
            return false;
        }

        this.name = newName;
        return true;
    }

    public String getAddress() {
        return this.address;
    }

    public boolean setAddress(final String newAddress) {
        if ((newAddress == null) || (newAddress.isBlank())) {
            return false;
        }

        var newAddressLength = newAddress.length();
        if ((newAddressLength < MinLengthAddress) || (newAddressLength > MaxLengthAddress)) {
            return false;
        }

        this.address = newAddress;
        return true;
    }

    public Set<Room> getRooms() {
        return this.rooms;
    }
}
