package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    @DisplayName("toItemDto — возвращает null при item = null")
    void toItemDto_shouldReturnNull_whenItemNull() {
        ItemDto result = ItemMapper.toItemDto(null, List.of());
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toItemDto — корректно маппит все поля при валидных данных")
    void toItemDto_shouldMapAllFields() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        ItemRequest request = new ItemRequest(2L, "Нужна вещь", owner, LocalDateTime.now());
        Item item = new Item(5L, "Молоток", "Инструмент", owner, true, request);

        Comment comment = new Comment(3L, "Комментарий", LocalDateTime.now(), item, owner);

        ItemDto result = ItemMapper.toItemDto(item, List.of(comment));

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Молоток");
        assertThat(result.getDescription()).isEqualTo("Инструмент");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(2L);
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getText()).isEqualTo("Комментарий");
    }

    @Test
    @DisplayName("toItemDto — comments = null и request = null")
    void toItemDto_shouldHandleNullCollections() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        Item item = new Item(5L, "Вещь", "Описание", owner, true, null);

        ItemDto result = ItemMapper.toItemDto(item, null);

        assertThat(result.getComments()).isEmpty();
        assertThat(result.getRequestId()).isNull();
    }

    @Test
    @DisplayName("toItemWithBookingsDto — возвращает null при item = null")
    void toItemWithBookingsDto_shouldReturnNull_whenItemNull() {
        ItemWithBookingsDto result = ItemMapper.toItemWithBookingsDto(null, null, null, null);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toItemWithBookingsDto — корректно маппит все поля и бронирования")
    void toItemWithBookingsDto_shouldMapAllFields() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        User booker = new User(2L, "Oleg", "oleg@mail.ru");
        Item item = new Item(10L, "Вещь", "Описание", owner, true, null);

        Booking last = new Booking();
        last.setId(11L);
        last.setBooker(booker);

        Booking next = new Booking();
        next.setId(12L);
        next.setBooker(booker);

        Comment comment = new Comment(3L, "Комментарий", LocalDateTime.now(), item, owner);

        ItemWithBookingsDto result = ItemMapper.toItemWithBookingsDto(item, last, next, List.of(comment));

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getLastBooking()).extracting(BookingShortDto::getId).isEqualTo(11L);
        assertThat(result.getNextBooking()).extracting(BookingShortDto::getId).isEqualTo(12L);
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    @DisplayName("toItemWithBookingsDto — обрабатывает null lastBooking/nextBooking/comments")
    void toItemWithBookingsDto_shouldHandleNulls() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        Item item = new Item(10L, "Вещь", "Описание", owner, true, null);

        ItemWithBookingsDto result = ItemMapper.toItemWithBookingsDto(item, null, null, null);

        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).isEmpty();
    }

    @Test
    @DisplayName("toItem — возвращает null при dto = null")
    void toItem_shouldReturnNull_whenDtoNull() {
        Item result = ItemMapper.toItem(null, new User(), new ItemRequest());
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toItem — корректно маппит поля при валидных данных")
    void toItem_shouldMapAllFields() {
        ItemCreateDto dto = new ItemCreateDto("Имя", "Описание", true, 5L);
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        ItemRequest request = new ItemRequest(2L, "Запрос", owner, LocalDateTime.now());

        Item result = ItemMapper.toItem(dto, owner, request);

        assertThat(result.getName()).isEqualTo("Имя");
        assertThat(result.getDescription()).isEqualTo("Описание");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequest()).isEqualTo(request);
        assertThat(result.getOwner()).isEqualTo(owner);
    }

    @Test
    @DisplayName("updateItem — ничего не делает при item = null или dto = null")
    void updateItem_shouldDoNothing_whenItemOrDtoNull() {
        ItemMapper.updateItem(null, new ItemUpdateDto(), new ItemRequest());
        ItemMapper.updateItem(new Item(), null, new ItemRequest());
    }

    @Test
    @DisplayName("updateItem — обновляет только непустые поля")
    void updateItem_shouldUpdateOnlyNonNullFields() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        ItemRequest request = new ItemRequest(2L, "Запрос", owner, LocalDateTime.now());
        Item item = new Item(5L, "Старая", "Старое описание", owner, false, null);

        ItemUpdateDto dto = new ItemUpdateDto("Новая", null, true, 2L);
        ItemMapper.updateItem(item, dto, request);

        assertThat(item.getName()).isEqualTo("Новая");
        assertThat(item.getDescription()).isEqualTo("Старое описание"); // не изменилось
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getRequest()).isEqualTo(request);
    }

}