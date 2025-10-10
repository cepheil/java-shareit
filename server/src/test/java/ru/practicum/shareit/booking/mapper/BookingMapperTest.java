package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("BookingMapper — тестирование преобразований DTO и моделей")
class BookingMapperTest {

    @Test
    @DisplayName("toBookingDto — корректно преобразует сущность в DTO")
    void toBookingDto_shouldMapAllFields() {
        User user = new User(1L, "Ivan", "ivan@mail.ru");
        Item item = new Item(2L, "Лопата", "Острая", user, true, null);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);

        Booking booking = new Booking();
        booking.setId(10L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(Status.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);

        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
        assertThat(dto.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(dto.getBooker().getId()).isEqualTo(1L);
        assertThat(dto.getItem().getId()).isEqualTo(2L);
        assertThat(dto.getItem().getName()).isEqualTo("Лопата");
    }

    @Test
    @DisplayName("toBookingDto — возвращает null при null-входных данных")
    void toBookingDto_shouldReturnNull_whenBookingNull() {
        assertThat(BookingMapper.toBookingDto(null)).isNull();
    }

    @Test
    @DisplayName("toBookingDto — корректно работает при null booker и item")
    void toBookingDto_shouldHandleNullFields() {
        Booking booking = new Booking();
        booking.setId(99L);
        booking.setStatus(Status.WAITING);

        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertThat(dto.getBooker()).isNull();
        assertThat(dto.getItem()).isNull();
        assertThat(dto.getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    @DisplayName("toBookingShortDto — корректно маппит короткий DTO")
    void toBookingShortDto_shouldMapCorrectly() {
        User user = new User(5L, "Test", "test@mail.ru");
        Booking booking = new Booking();
        booking.setId(123L);
        booking.setBooker(user);

        BookingShortDto shortDto = BookingMapper.toBookingShortDto(booking);

        assertThat(shortDto.getId()).isEqualTo(123L);
        assertThat(shortDto.getBookerId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("toBookingShortDto — возвращает null при null-входных данных")
    void toBookingShortDto_shouldReturnNull_whenNull() {
        assertThat(BookingMapper.toBookingShortDto(null)).isNull();
    }

    @Test
    @DisplayName("toBookingShortDto — корректно обрабатывает null booker")
    void toBookingShortDto_shouldHandleNullBooker() {
        Booking booking = new Booking();
        booking.setId(50L);

        BookingShortDto shortDto = BookingMapper.toBookingShortDto(booking);

        assertThat(shortDto.getBookerId()).isNull();
        assertThat(shortDto.getId()).isEqualTo(50L);
    }

    @Test
    @DisplayName("toBooking — корректно создаёт сущность из DTO")
    void toBooking_shouldMapFromDto() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);
        BookingCreateDto dto = new BookingCreateDto(start, end, 3L);

        User booker = new User(1L, "Ivan", "ivan@mail.ru");
        Item item = new Item(3L, "Зелёная вещь", "Описание", booker, true, null);

        Booking booking = BookingMapper.toBooking(dto, item, booker);

        assertThat(booking).isNotNull();
        assertThat(booking.getStart()).isEqualTo(start);
        assertThat(booking.getEnd()).isEqualTo(end);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
        assertThat(booking.getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    @DisplayName("toBooking — возвращает null при null-DTO")
    void toBooking_shouldReturnNull_whenDtoNull() {
        assertThat(BookingMapper.toBooking(null, null, null)).isNull();
    }
}