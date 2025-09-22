package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateDto {
    @NotNull(message = "Дата начала бронирования обязательна")
    @FutureOrPresent(message = "Дата начала должна быть в будущем или настоящем")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования обязательна")
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;

    @NotNull(message = "ID вещи обязателен")
    private Long itemId;

}
