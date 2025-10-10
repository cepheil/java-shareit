package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {

    @Test
    void gettersSetters_toString_shouldWork() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        ItemRequest request = new ItemRequest();
        request.setId(10L);

        Item item = new Item();
        item.setId(5L);
        item.setName("Тестовая вещь");
        item.setDescription("Описание вещи");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequest(request);

        assertThat(item.getId()).isEqualTo(5L);
        assertThat(item.getName()).isEqualTo("Тестовая вещь");
        assertThat(item.getDescription()).isEqualTo("Описание вещи");
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getRequest()).isEqualTo(request);

        String result = item.toString();
        assertThat(result).contains("Item");
        assertThat(result).contains("Тестовая вещь");
    }
}