package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.*;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> bookingDtoTester;

    @Autowired
    private JacksonTester<BookingCreateDto> bookingCreateDtoTester;


    @Test
    @DisplayName("Сериализация BookingDto в JSON")
    void serializeBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 10, 8, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 8, 12, 0);

        BookingDto dto = new BookingDto(
                1L,
                start,
                end,
                Status.WAITING,
                new BookerDto(5L),
                new ItemShortDto(9L, "Item")
        );

        JsonContent<BookingDto> json = bookingDtoTester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(json).extractingJsonPathStringValue("$.item.name").isEqualTo("Item");
        assertThat(json).extractingJsonPathNumberValue("$.booker.id").isEqualTo(5);

        assertThat(json).extractingJsonPathStringValue("$.start").startsWith("2025-10-08T10:00");
        assertThat(json).extractingJsonPathStringValue("$.end").startsWith("2025-10-08T12:00");
    }


    @Test
    @DisplayName("Десериализация JSON в BookingDto")
    void deserializeBookingDto() throws Exception {
        // @formatter:off
        String json = """
                {
                  "id": 1,
                  "start": "2025-10-08T10:00:00",
                  "end": "2025-10-08T12:00:00",
                  "status": "APPROVED",
                  "booker": { "id": 7 },
                  "item": { "id": 3, "name": "test Item" }
                }
                """;
        // @formatter:on

        BookingDto dto = bookingDtoTester.parseObject(json);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(dto.getItem().getName()).isEqualTo("test Item");
        assertThat(dto.getBooker().getId()).isEqualTo(7);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2025, 10, 8, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 10, 8, 12, 0));
    }


    @Test
    @DisplayName("Сериализация BookingCreateDto")
    void serializeBookingCreateDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 10, 9, 14, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 9, 16, 0);
        BookingCreateDto dto = new BookingCreateDto(start, end, 42L);

        JsonContent<BookingCreateDto> json = bookingCreateDtoTester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.itemId").isEqualTo(42);
        assertThat(json).extractingJsonPathStringValue("$.start").startsWith("2025-10-09T14:00");
        assertThat(json).extractingJsonPathStringValue("$.end").startsWith("2025-10-09T16:00");
    }

    @Test
    @DisplayName("Десериализация JSON в BookingCreateDto")
    void deserializeBookingCreateDto() throws Exception {
        // @formatter:off
        String json = """
                {
                  "start": "2025-10-10T09:00:00",
                  "end": "2025-10-10T11:00:00",
                  "itemId": 15
                }
                """;
        // @formatter:on
        BookingCreateDto dto = bookingCreateDtoTester.parseObject(json);

        assertThat(dto.getItemId()).isEqualTo(15);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2025, 10, 10, 9, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 10, 10, 11, 0));
    }
}