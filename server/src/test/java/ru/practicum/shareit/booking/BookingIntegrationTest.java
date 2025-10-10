package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));
        booker = userRepository.save(new User(null, "Booker", "booker@mail.com"));
        item = itemRepository.save(new Item(null, "Item", "Item description", owner, true, null));
    }


    @Test
    @DisplayName("Создание бронирования — успешный сценарий")
    void createBooking_shouldSaveSuccessfully() {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.plusHours(2);

        BookingCreateDto dto = new BookingCreateDto(start, end, item.getId());
        BookingDto result = bookingService.createBooking(booker.getId(), dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.WAITING);
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    @DisplayName("Создание бронирования — ошибка: бронирование своей вещи")
    void createBooking_shouldThrowWhenOwnItem() {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.plusHours(3);

        BookingCreateDto dto = new BookingCreateDto(start, end, item.getId());

        assertThrows(ForbiddenException.class,
                () -> bookingService.createBooking(owner.getId(), dto));
    }

    // 400 или 500 требуется ответ согласно тестам Postman
    @Test
    @DisplayName("Создание бронирования — ошибка: вещь недоступна")
    void createBooking_shouldThrowWhenItemUnavailable() {
        item.setAvailable(false);
        itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(1);

        BookingCreateDto dto = new BookingCreateDto(start, end, item.getId());
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booker.getId(), dto));
    }

    @Test
    @DisplayName("Создание бронирования — ошибка: дата окончания раньше начала")
    void createBooking_shouldThrowWhenEndBeforeStart() {
        LocalDateTime start = LocalDateTime.now().plusHours(3);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        BookingCreateDto dto = new BookingCreateDto(start, end, item.getId());
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booker.getId(), dto));
    }


    @Test
    @DisplayName("Подтверждение бронирования владельцем — успешный сценарий")
    void updateBooking_shouldApproveSuccessfully() {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.plusHours(1);

        BookingCreateDto dto = new BookingCreateDto(start, end, item.getId());
        BookingDto created = bookingService.createBooking(booker.getId(), dto);

        BookingDto updated = bookingService.updateBooking(owner.getId(), created.getId(), true);

        assertThat(updated.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    @DisplayName("Подтверждение бронирования — ошибка: не владелец вещи")
    void updateBooking_shouldThrowWhenNotOwner() {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.plusHours(1);

        BookingCreateDto dto = new BookingCreateDto(start, end, item.getId());
        BookingDto created = bookingService.createBooking(booker.getId(), dto);

        assertThrows(ForbiddenException.class,
                () -> bookingService.updateBooking(booker.getId(), created.getId(), true));
    }

    @Test
    @DisplayName("Повторное подтверждение бронирования — ошибка ValidationException")
    void updateBooking_shouldThrowWhenAlreadyApproved() {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.plusHours(1);

        BookingCreateDto dto = new BookingCreateDto(start, end, item.getId());
        BookingDto created = bookingService.createBooking(booker.getId(), dto);

        bookingService.updateBooking(owner.getId(), created.getId(), true);

        assertThrows(ConflictException.class,
                () -> bookingService.updateBooking(owner.getId(), created.getId(), true));
    }


    @Test
    @DisplayName("Получение бронирования — успешный сценарий (владелец)")
    void getBookingById_shouldReturnForOwner() {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.plusHours(1);
        BookingDto created = bookingService.createBooking(booker.getId(), new BookingCreateDto(start, end, item.getId()));

        BookingDto found = bookingService.getBookingById(owner.getId(), created.getId());
        assertThat(found.getId()).isEqualTo(created.getId());
    }


    @Test
    @DisplayName("Получение бронирования — ошибка: посторонний пользователь")
    void getBookingById_shouldThrowWhenNotOwnerOrBooker() {
        User other = userRepository.save(new User(null, "Other", "Other@mail.com"));
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.plusHours(1);
        BookingDto created = bookingService.createBooking(booker.getId(), new BookingCreateDto(start, end, item.getId()));

        assertThrows(ForbiddenException.class,
                () -> bookingService.getBookingById(other.getId(), created.getId()));
    }


    @Test
    @DisplayName("getUserBookings — возвращает список всех бронирований (ALL)")
    void getUserBookings_shouldReturnAll() {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.plusHours(1);
        bookingService.createBooking(booker.getId(), new BookingCreateDto(start, end, item.getId()));

        List<BookingDto> list = bookingService.getUserBookings(booker.getId(), "ALL");
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getStatus()).isEqualTo(Status.WAITING);
    }


    @Test
    @DisplayName("getOwnerBookings — возвращает бронирования вещей владельца")
    void getOwnerBookings_shouldReturnBookings() {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.plusHours(1);
        bookingService.createBooking(booker.getId(), new BookingCreateDto(start, end, item.getId()));

        List<BookingDto> list = bookingService.getOwnerBookings(owner.getId(), "ALL");
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    @DisplayName("getOwnerBookings — ошибка если владелец не найден")
    void getOwnerBookings_shouldThrowWhenOwnerNotFound() {
        assertThrows(NotFoundException.class,
                () -> bookingService.getOwnerBookings(999L, "ALL"));
    }
}