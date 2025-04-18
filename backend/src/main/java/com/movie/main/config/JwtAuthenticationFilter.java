package com.movie.main.config;

import java.util.ArrayList;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.movie.main.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @NotNull
    private final JwtTokenProvider tokenProvider;

    @NotNull
    private final UserService userService;

    private static final String BEARER_PREFIX = "Bearer ";

    public JwtAuthenticationFilter(
            @NotNull final JwtTokenProvider tokenProvider,
            @NotNull final UserService userService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    private static String getJwtFromRequest(@NotNull final HttpServletRequest request) {
        final var bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain) throws ServletException, java.io.IOException {
        final var token = getJwtFromRequest(request);

        if (token != null && tokenProvider.validateToken(token)) {
            final var username = tokenProvider.getUsernameFromJWT(token);
            final var user = userService.findEntityByUsername(username);
            if (user != null) {
                final var authentication = new UsernamePasswordAuthenticationToken(
                        user, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
