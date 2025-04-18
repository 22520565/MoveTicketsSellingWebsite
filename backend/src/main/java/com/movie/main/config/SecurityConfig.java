package com.movie.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.movie.main.service.UserService;

import jakarta.validation.constraints.NotNull;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @NotNull
    private final JwtTokenProvider tokenProvider;

    @NotNull
    private final UserService userService;

    public SecurityConfig(
            @NotNull final JwtTokenProvider tokenProvider,
            @NotNull final UserService userService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    @Bean
    SecurityFilterChain filterChain(@NotNull final HttpSecurity http) throws Exception {
        final var jwtFilter = new JwtAuthenticationFilter(tokenProvider, userService);

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
