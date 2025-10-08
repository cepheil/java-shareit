package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


class BookingTest {

    @Test
    void gettersSetters_toString_shouldBeCovered() {
        User owner = new User(2L, "Oleg", "oleg@mail.ru");
        User booker = new User(1L, "Ivan", "ivan@mail.ru");
        Item item = new Item(10L, "Зелёная вещь", "Описание", owner, true, null);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);

        Booking b = new Booking();
        b.setId(100L);
        b.setStart(start);
        b.setEnd(end);
        b.setStatus(Status.WAITING);
        b.setItem(item);
        b.setBooker(booker);

        assertThat(b.getId()).isEqualTo(100L);
        assertThat(b.getStart()).isEqualTo(start);
        assertThat(b.getEnd()).isEqualTo(end);
        assertThat(b.getStatus()).isEqualTo(Status.WAITING);
        assertThat(b.getItem()).isSameAs(item);
        assertThat(b.getBooker()).isSameAs(booker);

        assertThat(b.toString()).isNotBlank();
    }
}