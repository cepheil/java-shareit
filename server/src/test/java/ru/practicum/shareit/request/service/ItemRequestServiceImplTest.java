package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest request;


    @BeforeEach
    void initData() {
        user = new User(1L, "Ivan", "ivan@mail.ru");
        request = new ItemRequest(1L, "Требуется тестовая вещь", user, LocalDateTime.now());
    }


    @Test
    @DisplayName("createRequest — успешное создание запроса")
    void createRequest_shouldCreateSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = itemRequestService.createRequest(1L, new ItemRequestCreateDto("Требуется тестовая вещь"));

        assertThat(result.getDescription()).isEqualTo("Требуется тестовая вещь");
        verify(requestRepository, times(1)).save(any(ItemRequest.class));
    }


    @Test
    @DisplayName("createRequest — ошибка при userId = null")
    void createRequest_shouldThrowWhenUserIdNull() {
        assertThrows(ConflictException.class,
                () -> itemRequestService.createRequest(null, new ItemRequestCreateDto("desc")));
    }


    @Test
    @DisplayName("createRequest — ошибка при несуществующем пользователе")
    void createRequest_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(999L, new ItemRequestCreateDto("desc")));
    }


    @Test
    @DisplayName("getUserRequests — возвращает список запросов пользователя")
    void getUserRequests_shouldReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequesterIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).contains("тестовая");
    }


    @Test
    @DisplayName("getUserRequests — ошибка при userId = null")
    void getUserRequests_shouldThrowWhenNullId() {
        assertThrows(ConflictException.class,
                () -> itemRequestService.getUserRequests(null));
    }


    @Test
    @DisplayName("getRequestById — успешное получение запроса")
    void getRequestById_shouldReturnRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(List.of());

        ItemRequestDto result = itemRequestService.getRequestById(1L, 1L);
        assertThat(result.getId()).isEqualTo(1L);
    }


    @Test
    @DisplayName("getUserRequests — возвращает пустой список, если у пользователя нет запросов")
    void getUserRequests_shouldReturnEmptyListWhenNoRequests() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequesterIdOrderByCreatedDesc(1L)).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertThat(result).isEmpty();
        verify(itemRepository, never()).findByRequestIdIn(anyList());
    }


    @Test
    @DisplayName("getRequestById — ошибка если запрос не найден")
    void getRequestById_shouldThrowWhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 1L));
    }


    @Test
    @DisplayName("getRequestById — ошибка при userId или requestId = null")
    void getRequestById_shouldThrowWhenIdsAreNull() {
        assertThrows(ConflictException.class,
                () -> itemRequestService.getRequestById(null, 1L));
        assertThrows(ConflictException.class,
                () -> itemRequestService.getRequestById(1L, null));
    }


    @Test
    @DisplayName("getAllRequests — возвращает чужие запросы")
    void getAllRequests_shouldReturnOtherUsersRequests() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "Oleg", "oleg@mail.ru")));
        when(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(eq(2L), any(PageRequest.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(2L, 0, 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).contains("тестовая");
    }


    @Test
    @DisplayName("getAllRequests — ошибка при userId = null")
    void getAllRequests_shouldThrowWhenUserIdNull() {
        assertThrows(ConflictException.class,
                () -> itemRequestService.getAllRequests(null, 0, 10));
    }


    @Test
    @DisplayName("getAllRequests — ошибка если userId не найден")
    void getAllRequests_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllRequests(99L, 0, 10));
    }


}