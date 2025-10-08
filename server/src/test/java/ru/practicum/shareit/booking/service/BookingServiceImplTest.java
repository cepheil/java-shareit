package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Ivan", "ivan@mail.ru");
        owner = new User(2L, "Oleg", "oleg@mail.ru");
        item = new Item(10L, "Зелёная вещь", "Описание", owner, true, null);

        start = LocalDateTime.now().plusDays(1);
        end = start.plusDays(1);

        booking = new Booking();
        booking.setId(100L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(Status.WAITING);
    }


    @Test
    @DisplayName("createBooking — успешно создаёт бронирование")
    void createBooking_shouldCreateSuccessfully() {
        BookingCreateDto dto = new BookingCreateDto(start, end, item.getId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(user.getId(), dto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.WAITING);
        verify(bookingRepository).save(any(Booking.class));
    }


    @Test
    @DisplayName("createBooking — бросает ValidationException, если start после end")
    void createBooking_shouldThrow_whenStartAfterEnd() {
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto dto = new BookingCreateDto(now.plusDays(2), now.plusDays(1), 1L);

        assertThatThrownBy(() -> bookingService.createBooking(1L, dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("раньше даты окончания");
    }

    @Test
    @DisplayName("createBooking — бросает ValidationException, если start в прошлом")
    void createBooking_shouldThrow_whenStartInPast() {
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto dto = new BookingCreateDto(now.minusDays(1), now.plusDays(1), 1L);

        assertThatThrownBy(() -> bookingService.createBooking(1L, dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("в прошлом");
    }


    @Test
    @DisplayName("createBooking — бросает ForbiddenException, если пользователь бронирует свою вещь")
    void createBooking_shouldThrow_whenUserBooksOwnItem() {
        BookingCreateDto dto = new BookingCreateDto(start, end, item.getId());
        item.setOwner(user); // тот же владелец

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBooking(user.getId(), dto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("свою вещь");
    }

    @Test
    @DisplayName("createBooking — бросает ValidationException, если вещь недоступна")
    void createBooking_shouldThrow_whenItemUnavailable() {
        item.setAvailable(false);
        BookingCreateDto dto = new BookingCreateDto(start, end, item.getId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBooking(user.getId(), dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("недоступна");
    }

    @Test
    @DisplayName("createBooking — бросает ValidationException, если userId = null")
    void createBooking_shouldThrow_whenUserIdNull() {
        BookingCreateDto dto = new BookingCreateDto(start, end, 1L);
        assertThatThrownBy(() -> bookingService.createBooking(null, dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("ID пользователя не может быть null");
    }

    @Test
    @DisplayName("createBooking — бросает ValidationException, если start или end null")
    void createBooking_shouldThrow_whenDatesNull() {
        BookingCreateDto dto = new BookingCreateDto(null, null, 1L);
        assertThatThrownBy(() -> bookingService.createBooking(1L, dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Дата начала и окончания обязательны");
    }


    @Test
    @DisplayName("updateBooking — владелец подтверждает бронирование")
    void updateBooking_shouldApprove() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.updateBooking(owner.getId(), booking.getId(), true);

        assertThat(result.getStatus()).isEqualTo(Status.APPROVED);
        verify(bookingRepository).save(any());
    }

    @Test
    @DisplayName("updateBooking — бросает ForbiddenException, если не владелец")
    void updateBooking_shouldThrow_whenNotOwner() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateBooking(999L, booking.getId(), true))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Только владелец");
    }

    @Test
    @DisplayName("updateBooking — бросает ValidationException, если уже APPROVED")
    void updateBooking_shouldThrow_whenAlreadyApproved() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateBooking(owner.getId(), booking.getId(), true))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("уже подтверждено");
    }

    @Test
    @DisplayName("updateBooking — бросает ValidationException, если ownerId или bookingId null")
    void updateBooking_shouldThrow_whenIdsNull() {
        assertThatThrownBy(() -> bookingService.updateBooking(null, 1L, true))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("ID владельца и ID бронирования");
    }


    @Test
    @DisplayName("getBookingById — возвращает DTO, если запрос от владельца")
    void getBookingById_shouldReturn_whenOwnerOrBooker() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto dto = bookingService.getBookingById(owner.getId(), booking.getId());

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(booking.getId());
    }

    @Test
    @DisplayName("getBookingById — бросает ForbiddenException, если чужой пользователь")
    void getBookingById_shouldThrow_whenNotRelatedUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.of(new User(999L, "Other", "other@mail.ru")));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(999L, booking.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Только владелец");
    }


    @Nested
    @DisplayName("getUserBookings — выборки по состоянию")
    class GetUserBookingsTests {

        @BeforeEach
        void setupUser() {
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        }

        @Test
        void shouldReturnAll_whenStateAll() {
            when(bookingRepository.findByBookerIdOrderByStartDesc(user.getId()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getUserBookings(user.getId(), "ALL");
            assertThat(result).hasSize(1);
        }

        @Test
        void shouldReturnWaiting_whenStateWaiting() {
            when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.WAITING))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getUserBookings(user.getId(), "WAITING");
            assertThat(result).hasSize(1);
        }

        @Test
        void shouldThrow_whenUserNotFound() {
            when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.getUserBookings(user.getId(), "ALL"))
                    .isInstanceOf(NotFoundException.class);
        }


        @Test
        void shouldReturnCurrentBookings() {
            when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(user.getId()), any(), any()))
                    .thenReturn(List.of(booking));
            List<BookingDto> result = bookingService.getUserBookings(user.getId(), "CURRENT");
            assertThat(result).hasSize(1);
        }

        @Test
        void shouldReturnPastBookings() {
            when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(eq(user.getId()), any()))
                    .thenReturn(List.of(booking));
            List<BookingDto> result = bookingService.getUserBookings(user.getId(), "PAST");
            assertThat(result).hasSize(1);
        }

        @Test
        void shouldReturnFutureBookings() {
            when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(eq(user.getId()), any()))
                    .thenReturn(List.of(booking));
            List<BookingDto> result = bookingService.getUserBookings(user.getId(), "FUTURE");
            assertThat(result).hasSize(1);
        }

        @Test
        void shouldReturnRejectedBookings() {
            when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.REJECTED))
                    .thenReturn(List.of(booking));
            List<BookingDto> result = bookingService.getUserBookings(user.getId(), "REJECTED");
            assertThat(result).hasSize(1);
        }

    }

    @Nested
    class GetOwnerBookingsStates {

        @BeforeEach
        void setupOwner() {
            when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        }

        @Test
        void shouldReturnCurrentBookingsForOwner() {
            when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(owner.getId()), any(), any()))
                    .thenReturn(List.of(booking));
            List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), "CURRENT");
            assertThat(result).hasSize(1);
        }

        @Test
        void shouldReturnPastBookingsForOwner() {
            when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(owner.getId()), any()))
                    .thenReturn(List.of(booking));
            List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), "PAST");
            assertThat(result).hasSize(1);
        }

        @Test
        void shouldReturnFutureBookingsForOwner() {
            when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(eq(owner.getId()), any()))
                    .thenReturn(List.of(booking));
            List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), "FUTURE");
            assertThat(result).hasSize(1);
        }

        @Test
        void shouldReturnWaitingBookingsForOwner() {
            when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.WAITING))
                    .thenReturn(List.of(booking));
            List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), "WAITING");
            assertThat(result).hasSize(1);
        }

        @Test
        void shouldReturnRejectedBookingsForOwner() {
            when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.REJECTED))
                    .thenReturn(List.of(booking));
            List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), "REJECTED");
            assertThat(result).hasSize(1);
        }

    }
}