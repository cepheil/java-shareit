package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User otherUser;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));
        otherUser = userRepository.save(new User(null, "Other", "other@mail.com"));
    }


    @Test
    @DisplayName("Создание вещи — успешный сценарий")
    void createItem_shouldSaveAndReturnItem() {
        ItemCreateDto dto = new ItemCreateDto("test Item", "test Desc", true, null);

        ItemDto result = itemService.createItem(owner.getId(), dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("test Item");
        assertThat(itemRepository.findById(result.getId())).isPresent();
    }

    @Test
    @DisplayName("Создание вещи — ошибка если владелец не найден")
    void createItem_shouldThrowWhenOwnerNotFound() {
        ItemCreateDto dto = new ItemCreateDto("test Item", "test Desc", true, null);
        assertThrows(NotFoundException.class, () -> itemService.createItem(999L, dto));
    }


    @Test
    @DisplayName("Обновление вещи — успешный сценарий")
    void updateItem_shouldUpdateSuccessfully() {
        ItemDto created = itemService.createItem(owner.getId(),
                new ItemCreateDto("Old name", "Old desc", true, null));

        ItemUpdateDto update = new ItemUpdateDto("New name", "Updated desc", false, null);
        ItemDto updated = itemService.updateItem(owner.getId(), created.getId(), update);

        assertThat(updated.getName()).isEqualTo("New name");
        assertThat(updated.getAvailable()).isFalse();
    }


    @Test
    @DisplayName("Обновление вещи — ошибка если не владелец")
    void updateItem_shouldThrowWhenNotOwner() {
        ItemDto created = itemService.createItem(owner.getId(),
                new ItemCreateDto("Item", "Desc", true, null));

        ItemUpdateDto update = new ItemUpdateDto("Name", "Desc", true, null);

        assertThrows(ForbiddenException.class, () ->
                itemService.updateItem(otherUser.getId(), created.getId(), update));
    }


    @Test
    @DisplayName("Получение вещи по ID владельцем — возвращает с бронированиями")
    void getItemById_asOwner_shouldReturnItemWithBookings() {
        ItemDto created = itemService.createItem(owner.getId(),
                new ItemCreateDto("test Item", "test Desc", true, null));

        ItemWithBookingsDto result = itemService.getItemById(owner.getId(), created.getId());

        assertThat(result.getName()).isEqualTo("test Item");
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getComments()).isEmpty();
    }


    @Test
    @DisplayName("Получение вещи — ошибка если вещь не найдена")
    void getItemById_shouldThrowWhenNotFound() {
        assertThrows(NotFoundException.class, () ->
                itemService.getItemById(owner.getId(), 999L));
    }


    @Test
    @DisplayName("Получение всех вещей владельца — возвращает список")
    void getAllItemsByOwner_shouldReturnList() {
        itemService.createItem(owner.getId(), new ItemCreateDto("Item1", "Desc1", true, null));
        itemService.createItem(owner.getId(), new ItemCreateDto("Item2", "Desc2", true, null));

        Collection<ItemWithBookingsDto> result = itemService.getAllItemsByOwner(owner.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemWithBookingsDto::getName)
                .containsExactlyInAnyOrder("Item1", "Item2");
    }


    @Test
    @DisplayName("Получение всех вещей — ошибка если владелец не найден")
    void getAllItemsByOwner_shouldThrowWhenOwnerNotFound() {
        assertThrows(NotFoundException.class, () ->
                itemService.getAllItemsByOwner(999L));
    }


    @Test
    @DisplayName("Поиск вещей по тексту — возвращает совпадения")
    void searchItems_shouldReturnResults() {
        itemService.createItem(owner.getId(), new ItemCreateDto("Drill", "Power tool", true, null));
        itemService.createItem(owner.getId(), new ItemCreateDto("Hammer", "Steel hammer", true, null));

        Collection<ItemDto> foundDrill = itemService.searchItems("drill");
        Collection<ItemDto> foundHammer = itemService.searchItems("steel");

        assertThat(foundDrill).hasSize(1);
        assertThat(foundDrill.iterator().next().getName()).isEqualTo("Drill");

        assertThat(foundHammer).hasSize(1);
        assertThat(foundHammer.iterator().next().getName()).isEqualTo("Hammer");
    }

    @Test
    @DisplayName("Поиск вещей — пустой запрос возвращает пустой список")
    void searchItems_blankText_shouldReturnEmpty() {
        Collection<ItemDto> found = itemService.searchItems("  ");
        assertThat(found).isEmpty();
    }


    @Test
    @DisplayName("Удаление вещи — успешный сценарий")
    void deleteItem_shouldRemoveFromDB() {
        ItemDto created = itemService.createItem(owner.getId(),
                new ItemCreateDto("Item1", "Desc1", true, null));

        itemService.deleteItem(owner.getId(), created.getId());

        assertThat(itemRepository.findById(created.getId())).isEmpty();
    }


    @Test
    @DisplayName("Удаление вещи — ошибка если не владелец")
    void deleteItem_shouldThrowForbiddenWhenNotOwner() {
        ItemDto created = itemService.createItem(owner.getId(),
                new ItemCreateDto("Item1", "Desc1", true, null));

        assertThrows(ForbiddenException.class, () ->
                itemService.deleteItem(otherUser.getId(), created.getId()));
    }

    @Test
    @DisplayName("Удаление вещи — ошибка если ID = null")
    void deleteItem_shouldThrowValidationWhenIdNull() {
        assertThrows(ConflictException.class, () ->
                itemService.deleteItem(owner.getId(), null));
    }
}