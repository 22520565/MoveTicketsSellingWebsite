package com.movie.main.auth;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.movie.main.entity.UserDetailsInterface.UserRole;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider {
    private static final String rawSecret = "your-very-secure-secret-32bytes-minimum123!";
    protected static final SecretKey secretKey = Keys.hmacShaKeyFor(rawSecret.getBytes(StandardCharsets.UTF_8));

    @NotBlank
    public String generateToken(@NotBlank final String username, @NotBlank final UserRole userRole) {
        final var now = Instant.now();
        final var expiry = now.plus(Duration.ofHours(1));

        return Jwts.builder()
                .subject(username)
                .claim("ROLE", userRole.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    @NotNull
    public String getUsernameFromJWT(@NotNull final String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(@Nullable final String token) {
        try {
            if ((token == null) || token.isBlank()) {
                return false;
            }
            final var claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

            final var expiration = claims.getExpiration();
            return (expiration != null) && expiration.after(Date.from(Instant.now()));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return false;
        }
    }

    @NotNull
    public UserRole getRoleFromJWT(@NotBlank final String token) {
        final var roleStr = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("ROLE", String.class);

        return UserRole.valueOf(roleStr);
    }
}
