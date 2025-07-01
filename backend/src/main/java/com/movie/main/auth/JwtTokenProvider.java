package com.movie.main.auth;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import com.movie.main.entity.User;
import com.movie.main.entity.User.UserRole;
import com.movie.main.resource.ResourceStrings;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class JwtTokenProvider {
    public static final Duration TOKEN_DURATION = Duration.ofMinutes(60);
    private static final String RAW_SECRET = ResourceStrings.JWT_RAW_KEY;
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(RAW_SECRET.getBytes(StandardCharsets.UTF_8));

    private JwtTokenProvider() {}

    @NotBlank
    public static <TUSER extends User> String generateToken(@NotBlank final String username,
            @NotBlank final UserRole userRole) {
        final var now = Instant.now();
        final var expiry = now.plus(TOKEN_DURATION);

        return Jwts.builder()
                .subject(username)
                .claim("ROLE", userRole.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(SECRET_KEY)
                .compact();
    }

    @NotNull
    public static String getUsernameFromJWT(@NotNull final String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public static boolean validateToken(@Nullable final String token) {
        try {
            if ((token == null) || token.isBlank()) {
                return false;
            }
            final var claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();

            final var expiration = claims.getExpiration();
            return (expiration != null) && expiration.after(Date.from(Instant.now()));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return false;
        }
    }

    @Nullable
    public static UserRole getRoleFromJWT(@Nullable final String token) {
        if ((token == null) || token.isBlank()) {
            return null;
        }

        final var roleStr = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("ROLE", String.class);

        return UserRole.valueOf(roleStr);
    }
}
