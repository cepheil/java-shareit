package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


class ErrorHandlerTest {
    private final ErrorHandler handler = new ErrorHandler();


    @Test
    @DisplayName("Обрабатывает NotFoundException (404)")
    void handleNotFoundException() {
        NotFoundException ex = new NotFoundException("Объект не найден");
        ErrorResponse response = handler.handlerNotFoundException(ex);

        assertThat(response.getError()).isEqualTo("NotFoundException");
        assertThat(response.getDescription()).contains("не найден");
    }

    @Test
    @DisplayName("Обрабатывает ValidationException (400)")
    void handleValidationException() {
        ValidationException ex = new ValidationException("Некорректные данные");
        ErrorResponse response = handler.handlerValidationException(ex);

        assertThat(response.getError()).isEqualTo("ValidationException");
        assertThat(response.getDescription()).contains("Некорректные");
    }

    @Test
    @DisplayName("Обрабатывает ForbiddenException (403)")
    void handleForbiddenException() {
        ForbiddenException ex = new ForbiddenException("Доступ запрещён");
        ErrorResponse response = handler.handleForbiddenException(ex);

        assertThat(response.getError()).isEqualTo("ForbiddenException");
        assertThat(response.getDescription()).contains("запрещён");
    }

    @Test
    @DisplayName("Обрабатывает ConflictException (409)")
    void handleConflictException() {
        ConflictException ex = new ConflictException("Конфликт данных");
        ErrorResponse response = handler.handleConflictException(ex);

        assertThat(response.getError()).isEqualTo("ConflictException");
        assertThat(response.getDescription()).contains("Конфликт");
    }

    @Test
    @DisplayName("Обрабатывает ConstraintViolationException (400)")
    void handleConstraintViolationException() {
        ConstraintViolationException ex = new ConstraintViolationException("Ошибка валидации", null);
        ErrorResponse response = handler.handleConstraintViolationException(ex);

        assertThat(response.getError()).isEqualTo("ConstraintViolationException");
        assertThat(response.getDescription()).contains("валидации");
    }

    @Test
    @DisplayName("Обрабатывает MethodArgumentNotValidException (400)")
    void handleMethodArgumentNotValidException() throws Exception {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        ErrorResponse response = handler.handlerMethodArgumentNotValidException(ex);

        assertThat(response.getError()).isEqualTo("MethodArgumentNotValidException");
    }

    @Test
    @DisplayName("Обрабатывает любые Exception (500)")
    void handleOtherExceptions() {
        Exception ex = new RuntimeException("Неизвестная ошибка");
        ErrorResponse response = handler.handleOtherExceptions(ex);

        assertThat(response.getError()).isEqualTo("RuntimeException");
        assertThat(response.getDescription()).contains("Неизвестная");
    }
}