package com.movie.main.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public final class GlobalExceptionHandler {
    private GlobalExceptionHandler() {
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public static ResponseEntity<Void> handleHandlerMethodValidationException(
            final HandlerMethodValidationException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public static ResponseEntity<Void> handleValidationException(final MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public static ResponseEntity<Void> handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public static ResponseEntity<Void> handleConstraintViolationException(
            final ConstraintViolationException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public static ResponseEntity<Void> handleHttpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public static ResponseEntity<Void> handleDataIntegrityViolationException(
            final DataIntegrityViolationException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public static ResponseEntity<Void> handleNoResourceFoundException(
            final NoResourceFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Throwable.class)
    public static ResponseEntity<Void> handleThrowable(final Throwable throwable) {
        log.error(throwable.getMessage());
        return ResponseEntity.internalServerError().build();
    }
}
