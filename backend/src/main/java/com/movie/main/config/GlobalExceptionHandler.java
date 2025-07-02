package com.movie.main.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.movie.main.dto.response.ErrorResponseDto;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public final class GlobalExceptionHandler {
    private GlobalExceptionHandler() {}

    @ExceptionHandler(HandlerMethodValidationException.class)
    public static ResponseEntity<ErrorResponseDto> handleHandlerMethodValidationException(
            final HandlerMethodValidationException exception) {
        final var errorResponseDto = new ErrorResponseDto(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponseDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public static ResponseEntity<ErrorResponseDto> handleValidationException(
            final MethodArgumentNotValidException exception) {
        final var errorResponseDto = new ErrorResponseDto(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponseDto);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public static ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException exception) {
        final var errorResponseDto = new ErrorResponseDto(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponseDto);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public static ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
            final ConstraintViolationException exception) {
        final var errorResponseDto = new ErrorResponseDto(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponseDto);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public static ResponseEntity<ErrorResponseDto> handleHttpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException exception) {
        final var errorResponseDto = new ErrorResponseDto(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponseDto);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public static ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(
            final DataIntegrityViolationException exception) {
        final var errorResponseDto = new ErrorResponseDto(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponseDto);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public static ResponseEntity<ErrorResponseDto> handleNoResourceFoundException(
            final NoResourceFoundException exception) {
        final var errorResponseDto = new ErrorResponseDto(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public static ResponseEntity<ErrorResponseDto> handleRsponseStatusException(
            final ResponseStatusException exception) {
        final var body = new ErrorResponseDto(exception.getReason());
        return ResponseEntity.status(exception.getStatusCode()).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public static ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException exception) {
        final var errorResponseDto = new ErrorResponseDto(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponseDto);
    }

    @ExceptionHandler(Throwable.class)
    public static ResponseEntity<Void> handleThrowable(final Throwable throwable) {
        log.error(throwable.getMessage());
        return ResponseEntity.internalServerError().build();
    }
}
