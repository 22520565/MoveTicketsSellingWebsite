package com.movie.main.auth;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.movie.main.exception.UnauthorizedException;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.DenyAll;

@Aspect
@Component
public class RequirePermissionAspect {
    @Before("@within(requirePermission) || @annotation(requirePermission)")
    public void checkPermissions(final JoinPoint joinPoint, final RequirePermission requirePermission) {
        final Class<?> targetClass = joinPoint.getTarget().getClass();
        final String methodName = joinPoint.getSignature().getName();

        final Method method = Arrays.stream(targetClass.getMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst()
                .orElse(null);

        // 1. Method-level @RequirePermission → always enforced
        final RequirePermission methodPermission = getAnnotation(method, RequirePermission.class);
        if (methodPermission != null) {
            performCheck(methodPermission);
            return;
        }

        // 2. Method-level @PermitAll / @DenyAll → skip
        if (hasAnnotation(method, PermitAll.class) || hasAnnotation(method, DenyAll.class)) {
            return;
        }

        // 3. Class-level @PermitAll / @DenyAll → skip
        if (hasAnnotation(targetClass, PermitAll.class) || hasAnnotation(targetClass, DenyAll.class)) {
            return;
        }

        // 4. Class-level @RequirePermission → enforced
        final RequirePermission classPermission = (requirePermission != null) ? requirePermission
                : getAnnotation(targetClass, RequirePermission.class);

        if (classPermission != null) {
            performCheck(classPermission);
        }
    }

    private void performCheck(final RequirePermission permission) {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            throw new AccessDeniedException("No authentication found");
        }

        final String requiredPermission = "PERMISSION_" + permission.value().name();
        final boolean hasPermission = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals(requiredPermission));

        if (!hasPermission) {
            throw new UnauthorizedException("Permission denied: missing required permission");
        }
    }

    private <T extends java.lang.annotation.Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
        return (method != null) ? AnnotationUtils.findAnnotation(method, annotationClass) : null;
    }

    private <T extends java.lang.annotation.Annotation> T getAnnotation(Class<?> cls, Class<T> annotationClass) {
        return AnnotationUtils.findAnnotation(cls, annotationClass);
    }

    private boolean hasAnnotation(Method method, Class<? extends java.lang.annotation.Annotation> annotationClass) {
        return getAnnotation(method, annotationClass) != null;
    }

    private boolean hasAnnotation(Class<?> cls, Class<? extends java.lang.annotation.Annotation> annotationClass) {
        return getAnnotation(cls, annotationClass) != null;
    }
}
