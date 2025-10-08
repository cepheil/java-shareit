package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestTest {

    @Test
    void gettersSetters_toString_shouldWork() {
        User requester = new User(1L, "Ivan", "ivan@mail.ru");
        LocalDateTime created = LocalDateTime.now();

        ItemRequest request = new ItemRequest();
        request.setId(10L);
        request.setDescription("Нужна зелёная вещь");
        request.setRequester(requester);
        request.setCreated(created);

        assertThat(request.getId()).isEqualTo(10L);
        assertThat(request.getDescription()).isEqualTo("Нужна зелёная вещь");
        assertThat(request.getRequester()).isSameAs(requester);
        assertThat(request.getCreated()).isEqualTo(created);

        String text = request.toString();
        assertThat(text).contains("ItemRequest");
        assertThat(text).contains("Нужна зелёная вещь");
    }
}