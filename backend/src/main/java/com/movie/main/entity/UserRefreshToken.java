package com.movie.main.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class UserRefreshToken {
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(7);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true)
    @Setter(value = AccessLevel.PACKAGE)
    @NotNull
    private UUID id = null;

    @OneToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY)
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
