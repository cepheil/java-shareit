package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)  //404
    public ErrorResponse handlerNotFoundException(NotFoundException e) {
        log.warn("NotFoundException: {}", e.getMessage());
        return buildErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)   //400
    public ErrorResponse handlerValidationException(ValidationException e) {
        log.warn("ValidationException: {}", e.getMessage());
        return buildErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    public ErrorResponse handleForbiddenException(ForbiddenException e) {
        log.warn("ForbiddenException: {}", e.getMessage());
        return buildErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return buildErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorResponse handleConflictException(ConflictException e) {
        log.warn("ConflictException: {}", e.getMessage());
        return buildErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)   //400
    public ErrorResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());
        return buildErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("ConstraintViolationException: {}", e.getMessage());
        return buildErrorResponse(e);
    }


    private ErrorResponse buildErrorResponse(Exception e) {
        return new ErrorResponse(
                e.getClass().getSimpleName(),
                e.getMessage());
    }
}
