package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    @Test
    @DisplayName("toItemRequestDto возвращает null при null входных данных")
    void toItemRequestDto_shouldReturnNull_whenRequestIsNull() {
        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(null, List.of());
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toItemRequestDto корректно обрабатывает null items")
    void toItemRequestDto_shouldHandleNullItems() {
        ItemRequest request = new ItemRequest(
                1L,
                "Нужна тестовая вещь",
                new User(1L, "Ivan", "ivan@mail.ru"),
                LocalDateTime.now()
        );

        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(request, null);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Нужна тестовая вещь");
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    @DisplayName("toItemRequestDto корректно маппит все поля при непустом списке")
    void toItemRequestDto_shouldMapAllFields() {
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request = new ItemRequest(
                2L,
                "Запрос с вещами",
                new User(2L, "Oleg", "oleg@mail.ru"),
                created
        );
        List<ItemDto> items = List.of(new ItemDto(1L, "Вещь", "Описание", true, null, List.of()));

        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(request, items);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getCreated()).isEqualTo(created);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Вещь");
    }

    @Test
    @DisplayName("toItemRequest возвращает null при dto = null")
    void toItemRequest_shouldReturnNull_whenDtoIsNull() {
        ItemRequest result = ItemRequestMapper.toItemRequest(null, new User());
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toItemRequest корректно маппит поля при валидных данных")
    void toItemRequest_shouldMapAllFields() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Хочу красную вещь");
        User requester = new User(3L, "Petr", "petr@mail.ru");

        ItemRequest result = ItemRequestMapper.toItemRequest(dto, requester);

        assertThat(result.getDescription()).isEqualTo("Хочу красную вещь");
        assertThat(result.getRequester()).isEqualTo(requester);
        assertThat(result.getCreated()).isNotNull();
    }
}