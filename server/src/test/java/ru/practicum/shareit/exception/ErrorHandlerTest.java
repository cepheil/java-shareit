package ru.practicum.shareit.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;


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
    @DisplayName("Обрабатывает любые Exception (500)")
    void handleOtherExceptions() {
        Exception ex = new RuntimeException("Неизвестная ошибка");
        ErrorResponse response = handler.handleOtherExceptions(ex);

        assertThat(response.getError()).isEqualTo("RuntimeException");
        assertThat(response.getDescription()).contains("Неизвестная");
    }
}