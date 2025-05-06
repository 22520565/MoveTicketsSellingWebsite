package com.movie.main.config;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.movie.main.auth.JwtTokenProvider;
import com.movie.main.service.EmployeeService;
import com.movie.main.service.UserService;

import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    @NotNull
    private final JwtTokenProvider tokenProvider;

    @NotNull
    private final UserService userService;

    @NotNull
    private final EmployeeService employeeService;

    public JwtAuthenticationFilter(@NotNull final JwtTokenProvider tokenProvider,
            @NotNull final UserService userService, @NotNull final EmployeeService employeeService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.employeeService = employeeService;
    }

    @Nullable
    private static String getJwtFromRequest(@NotNull final HttpServletRequest request) {
        final var bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response, @NonNull final FilterChain filterChain)
            throws ServletException, java.io.IOException {
        final var token = getJwtFromRequest(request);

        if (token == null || (!this.tokenProvider.validateToken(token))) {
            filterChain.doFilter(request, response);
            return;
        }

        final var username = this.tokenProvider.getUsernameFromJWT(token);
        final var user = this.userService.findEntityByUsername(username);
        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        final var role = this.tokenProvider.getRoleFromJWT(token);
        final var roleAuthority = new SimpleGrantedAuthority("ROLE_" + role);
        final var employee = this.employeeService.findEntityById(user.getId());

        final List<SimpleGrantedAuthority> permissionAuthorities;
        if (employee != null) {
            permissionAuthorities = employee.getPermissions()
                    .stream()
                    .map(permission -> new SimpleGrantedAuthority("PERMISSION_" + permission.name()))
                    .toList();
        }
        else {
            permissionAuthorities = Collections.emptyList();
        }

        final var authorities = Stream.concat(Stream.of(roleAuthority), permissionAuthorities.stream()).toList();
        final var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
