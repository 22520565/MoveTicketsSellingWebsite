package com.movie.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import com.movie.main.auth.JwtTokenProvider;
import com.movie.main.service.EmployeeService;
import com.movie.main.service.UserService;

import jakarta.validation.constraints.NotNull;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @NotNull
    private final JwtTokenProvider tokenProvider;

    @NotNull
    private final UserService userService;

    @NotNull
    private final EmployeeService employeeService;

    public SecurityConfig(@NotNull final JwtTokenProvider tokenProvider, @NotNull final UserService userService,
            @NotNull final EmployeeService employeeService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.employeeService = employeeService;
    }

    @Bean
    SecurityFilterChain filterChain(@NotNull final HttpSecurity http) throws Exception {
        final var jwtFilter = new JwtAuthenticationFilter(tokenProvider, userService, employeeService);

        return http.csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.contentTypeOptions(Customizer.withDefaults())
                        .frameOptions(FrameOptionsConfig::deny)
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true))
                        .addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy",
                                "default-src 'self'; " + "script-src 'self'; " + "style-src 'self'; "
                                        + "img-src 'self' data:; " + "font-src 'self'; " + "object-src 'none'; "
                                        + "frame-ancestors 'none'; " + "base-uri 'self'; " + "form-action 'self';")))
                // .authorizeHttpRequests(auth -> auth.anyRequest().denyAll())
                // .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
