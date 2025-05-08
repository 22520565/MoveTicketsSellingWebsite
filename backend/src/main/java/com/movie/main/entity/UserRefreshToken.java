package com.movie.main.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
public class UserRefreshToken extends IntegerIdentifiableEntity {
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(7);

    @Column(nullable = false, unique = true)
    @NotNull
    private UUID refreshToken = UUID.randomUUID();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @NotNull
    private User user = null;

    @Column(nullable = false)
    @NotNull
    private Instant expiryDate = Instant.now().plus(REFRESH_TOKEN_DURATION);

    public UserRefreshToken(final User user) {
        this.user = user;
    }
}
