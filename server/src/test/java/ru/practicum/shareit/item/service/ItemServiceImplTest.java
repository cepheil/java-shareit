package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@mail.com");
        item = new Item(10L, "Item", "Item description", owner, true, null);
    }


    @Test
    @DisplayName("Создание вещи — успешный сценарий без запроса")
    void createItem_shouldCreateSuccessfully() {
        ItemCreateDto dto = new ItemCreateDto("Item", "Item description", true, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(commentRepository.findByItemId(10L)).thenReturn(List.of());

        ItemDto result = itemService.createItem(1L, dto);

        assertThat(result.getName()).isEqualTo("Item");
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("Создание вещи — владелец не найден")
    void createItem_shouldThrowWhenOwnerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ItemCreateDto dto = new ItemCreateDto("Item", "Item description", true, null);

        assertThrows(NotFoundException.class, () -> itemService.createItem(1L, dto));
    }

    @Test
    @DisplayName("Создание вещи — requestId указан, но запрос не найден")
    void createItem_shouldThrowWhenRequestNotFound() {
        ItemCreateDto dto = new ItemCreateDto("Item", "Item description", true, 5L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(1L, dto));
    }


    @Test
    @DisplayName("Обновление вещи — успешный сценарий")
    void updateItem_shouldUpdateSuccessfully() {
        ItemUpdateDto dto = new ItemUpdateDto("Updated", "Desc", false, null);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(commentRepository.findByItemId(10L)).thenReturn(List.of());

        ItemDto result = itemService.updateItem(1L, 10L, dto);

        assertThat(result.getName()).isEqualTo("Updated");
        verify(itemRepository).save(item);
    }

    @Test
    @DisplayName("Обновление вещи — не владелец вызывает ForbiddenException")
    void updateItem_shouldThrowForbiddenWhenNotOwner() {
        User another = new User(2L, "Other", "o@mail.com");
        item.setOwner(owner);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class,
                () -> itemService.updateItem(another.getId(), 10L, new ItemUpdateDto()));
    }

    @Test
    @DisplayName("Обновление вещи — вещь не найдена")
    void updateItem_shouldThrowNotFound() {
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 10L, new ItemUpdateDto()));
    }


    @Test
    @DisplayName("Получение вещи по ID владельцем — содержит бронирования")
    void getItemById_asOwner_shouldIncludeBookings() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(10L)).thenReturn(List.of());
        Booking last = new Booking(100L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, owner, Status.APPROVED);
        when(bookingRepository.findLastBooking(eq(10L), any(), eq(Status.APPROVED), any(PageRequest.class)))
                .thenReturn(List.of(last));
        when(bookingRepository.findNextBooking(eq(10L), any(), eq(Status.APPROVED), any(PageRequest.class)))
                .thenReturn(List.of());

        ItemWithBookingsDto dto = itemService.getItemById(1L, 10L);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getLastBooking()).isNotNull();
    }

    @Test
    @DisplayName("Получение вещи — не владелец, без бронирований")
    void getItemById_asOtherUser_shouldHideBookings() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(10L)).thenReturn(List.of());

        ItemWithBookingsDto dto = itemService.getItemById(2L, 10L);

        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNull();
    }

    @Test
    @DisplayName("Поиск по тексту — пустая строка возвращает пустой список")
    void searchItems_blankQuery_returnsEmptyList() {
        assertThat(itemService.searchItems("   ")).isEmpty();
    }

    @Test
    @DisplayName("Поиск по тексту — успешный сценарий")
    void searchItems_shouldReturnFoundItems() {
        when(itemRepository.search("item")).thenReturn(List.of(item));
        when(commentRepository.findByItemIds(List.of(10L))).thenReturn(List.of());

        List<ItemDto> result = (List<ItemDto>) itemService.searchItems("item");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Item");
    }


    @Test
    @DisplayName("Удаление вещи — успешный сценарий")
    void deleteItem_shouldDeleteSuccessfully() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        itemService.deleteItem(1L, 10L);

        verify(itemRepository).deleteById(10L);
    }

    @Test
    @DisplayName("Удаление вещи — не владелец выбрасывает ForbiddenException")
    void deleteItem_shouldThrowForbidden() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class,
                () -> itemService.deleteItem(2L, 10L));
    }

    @Test
    @DisplayName("Удаление вещи — не найдена")
    void deleteItem_shouldThrowNotFound() {
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.deleteItem(1L, 10L));
    }


    @Test
    @DisplayName("createItem — ownerId == null выбрасывает ConflictException")
    void createItem_shouldThrowValidationWhenOwnerIdNull() {
        assertThrows(ConflictException.class,
                () -> itemService.createItem(null, new ItemCreateDto("x", "y", true, null)));
    }

    @Test
    @DisplayName("getItemById — itemId == null выбрасывает ConflictException")
    void getItemById_shouldThrowValidationWhenIdNull() {
        assertThrows(ConflictException.class,
                () -> itemService.getItemById(1L, null));
    }

    @Test
    @DisplayName("getAllItemsByOwner — ownerId == null выбрасывает ConflictException")
    void getAllItemsByOwner_shouldThrowValidationWhenNull() {
        assertThrows(ConflictException.class,
                () -> itemService.getAllItemsByOwner(null));
    }


}