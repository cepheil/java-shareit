package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(null, "Ivan", "ivan@mail.ru"));
        user2 = userRepository.save(new User(null, "Oleg", "oleg@mail.ru"));
    }


    @Test
    @DisplayName("Создание запроса — успешный сценарий")
    void createRequest_shouldCreateSuccessfully() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Требуется тестовая вещь");
        ItemRequestDto created = itemRequestService.createRequest(user1.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Требуется тестовая вещь");
        assertThat(itemRequestRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Создание запроса — ошибка при userId = null")
    void createRequest_shouldThrowWhenUserIdNull() {
        assertThrows(ValidationException.class,
                () -> itemRequestService.createRequest(null, new ItemRequestCreateDto("test")));
    }

    @Test
    @DisplayName("Создание запроса — ошибка при несуществующем пользователе")
    void createRequest_shouldThrowWhenUserNotFound() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(999L, new ItemRequestCreateDto("test")));
    }


    @Test
    @DisplayName("getUserRequests — возвращает список запросов пользователя")
    void getUserRequests_shouldReturnList() {
        itemRequestService.createRequest(user1.getId(), new ItemRequestCreateDto("Требуется зеленая вещь"));
        itemRequestService.createRequest(user1.getId(), new ItemRequestCreateDto("Требуется красная вещь"));

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(user1.getId());

        assertThat(requests).hasSize(2);
        List<String> descriptions = requests.stream()
                .map(ItemRequestDto::getDescription)
                .collect(Collectors.toList());
        assertThat(descriptions).contains("Требуется зеленая вещь", "Требуется красная вещь");
    }

    @Test
    @DisplayName("getUserRequests — ошибка при null userId")
    void getUserRequests_shouldThrowWhenNullId() {
        assertThrows(ValidationException.class,
                () -> itemRequestService.getUserRequests(null));
    }


    @Test
    @DisplayName("getRequestById — успешный сценарий")
    void getRequestById_shouldReturnRequest() {
        ItemRequestDto created = itemRequestService.createRequest(user1.getId(),
                new ItemRequestCreateDto("Требуется электрошвабра"));
        ItemRequestDto found = itemRequestService.getRequestById(user1.getId(), created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getDescription()).isEqualTo("Требуется электрошвабра");
    }

    @Test
    @DisplayName("getRequestById — ошибка при несуществующем запросе")
    void getRequestById_shouldThrowWhenNotFound() {
        itemRequestService.createRequest(user1.getId(), new ItemRequestCreateDto("Требуется зеленая вещь"));
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(user1.getId(), 999L));
    }


    @Test
    @DisplayName("getAllRequests — возвращает запросы других пользователей")
    void getAllRequests_shouldReturnOtherUsersRequests() {
        // user1 создаёт запрос
        itemRequestService.createRequest(user1.getId(), new ItemRequestCreateDto("Требуется зеленая вещь"));
        itemRequestService.createRequest(user1.getId(), new ItemRequestCreateDto("Требуется красная вещь"));

        // user2 запрашивает все чужие
        List<ItemRequestDto> result = itemRequestService.getAllRequests(user2.getId(), 0, 10);

        assertThat(result).hasSize(2);
        List<String> descriptions = result.stream()
                .map(ItemRequestDto::getDescription)
                .collect(Collectors.toList());
        assertThat(descriptions).contains("Требуется зеленая вещь", "Требуется красная вещь");
    }

    @Test
    @DisplayName("getAllRequests — ошибка при несуществующем пользователе")
    void getAllRequests_shouldThrowWhenUserNotFound() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllRequests(777L, 0, 10));
    }

    @Test
    @DisplayName("getAllRequests — пустой результат, если чужих нет")
    void getAllRequests_shouldReturnEmptyWhenNoOtherRequests() {
        itemRequestService.createRequest(user1.getId(), new ItemRequestCreateDto("Требуется вещь"));
        List<ItemRequestDto> result = itemRequestService.getAllRequests(user1.getId(), 0, 10);
        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("getUserRequests — возвращает связанные предметы для запроса")
    void getUserRequests_shouldReturnItemsInRequest() {
        // user1 создаёт запрос
        ItemRequestDto req = itemRequestService.createRequest(user1.getId(), new ItemRequestCreateDto("Требуется молоток"));

        // user2 добавляет вещь, связанную с этим запросом
        Item item = new Item(null, "Hammer", "Стальной молоток", user2, true,
                itemRequestRepository.findById(req.getId()).get());
        itemRepository.save(item);

        List<ItemRequestDto> result = itemRequestService.getUserRequests(user1.getId());
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getName()).isEqualTo("Hammer");
    }
}