package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemShortDto;
import ru.practicum.shareit.booking.service.BookingService;


import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;


    @Test
    @DisplayName("POST /bookings — успешное создание бронирования")
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        BookingCreateDto dto = new BookingCreateDto(start, end, 5L);

        BookingDto expected = new BookingDto(
                10L,
                start,
                end,
                Status.WAITING,
                new BookerDto(1L),
                new ItemShortDto(5L, "Item")
        );

        when(bookingService.createBooking(anyLong(), any(BookingCreateDto.class)))
                .thenReturn(expected);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(5))
                .andExpect(jsonPath("$.booker.id").value(1));
    }

    @Test
    @DisplayName("PATCH /bookings/{id}?approved=true — успешное подтверждение бронирования")
    void updateBooking_shouldReturnUpdated() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(1);

        BookingDto updated = new BookingDto(
                5L,
                start,
                end,
                Status.APPROVED,
                new BookerDto(2L),
                new ItemShortDto(9L, "Item2")
        );

        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(updated);

        mockMvc.perform(patch("/bookings/{id}?approved=true", 5L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.name").value("Item2"));
    }

    @Test
    @DisplayName("GET /bookings/{id} — успешное получение бронирования")
    void getBookingById_shouldReturnBooking() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        BookingDto dto = new BookingDto(
                1L,
                start,
                end,
                Status.WAITING,
                new BookerDto(3L),
                new ItemShortDto(7L, "Item3")
        );

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(dto);

        mockMvc.perform(get("/bookings/{id}", 1L)
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.name").value("Item3"));
    }

    @Test
    @DisplayName("GET /bookings?state=ALL — возвращает список бронирований пользователя")
    void getUserBookings_shouldReturnList() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        List<BookingDto> bookings = List.of(
                new BookingDto(1L, now, now.plusHours(2), Status.WAITING,
                        new BookerDto(1L), new ItemShortDto(5L, "Item1")),
                new BookingDto(2L, now.plusDays(1), now.plusDays(2), Status.APPROVED,
                        new BookerDto(1L), new ItemShortDto(6L, "Item2"))
        );

        when(bookingService.getUserBookings(anyLong(), any()))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));
    }

    @Test
    @DisplayName("GET /bookings/owner?state=ALL — возвращает список бронирований владельца")
    void getOwnerBookings_shouldReturnList() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        List<BookingDto> bookings = List.of(
                new BookingDto(3L, now, now.plusHours(3), Status.REJECTED,
                        new BookerDto(4L), new ItemShortDto(9L, "Item1"))
        );

        when(bookingService.getOwnerBookings(anyLong(), any()))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("REJECTED"))
                .andExpect(jsonPath("$[0].item.name").value("Item1"));
    }

    @Nested
    @DisplayName("Проверка обязательных заголовков")
    class ValidationTests {

        @Test
        @DisplayName("POST /bookings — выбрасывает MissingRequestHeaderException при отсутствии X-Sharer-User-Id")
        void createBooking_shouldThrowWhenNoHeader() throws Exception {
            LocalDateTime start = LocalDateTime.now().plusHours(1);
            LocalDateTime end = start.plusHours(2);
            BookingCreateDto dto = new BookingCreateDto(start, end, 1L);

            mockMvc.perform(post("/bookings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(org.springframework.web.bind.MissingRequestHeaderException.class)
                                    .hasMessageContaining("X-Sharer-User-Id"));
        }
    }


}