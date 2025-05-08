package com.movie.main.auth;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.movie.main.exception.UnauthorizedException;

@Aspect
@Component
public class PermissionAspect {
    @Before("@annotation(requirePermissions)")
    public void checkPermissions(final JoinPoint joinPoint, final RequirePermissions requirePermissions) {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication == null) || authentication.getAuthorities() == null) {
            throw new AccessDeniedException("No authentication found");
        }

        final Set<String> requiredPermissions = Arrays.stream(requirePermissions.value())
                .map(permission -> "PERMISSION_" + permission.name())
                .collect(Collectors.toSet());

        final var userAuthorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        boolean hasAnyPermission = userAuthorities.stream().anyMatch(requiredPermissions::contains);

        if (!hasAnyPermission) {
            throw new UnauthorizedException("Permission denied: missing required permissions");
        }
    }
}
