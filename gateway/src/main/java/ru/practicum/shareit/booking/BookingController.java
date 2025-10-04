package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import ru.practicum.shareit.booking.dto.BookingCreateDto;


@Validated
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingCreateDto dto
    ) {
        log.info("GATEWAY → POST /bookings — создание бронирования пользователем ID={}", userId);
        return bookingClient.createBooking(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable @Positive Long bookingId,
            @RequestParam boolean approved
    ) {
        log.info("GATEWAY → PATCH /bookings/{}?approved={} — изменение статуса пользователем ID={}", bookingId, approved, ownerId);
        return bookingClient.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable @Positive Long bookingId
    ) {
        log.info("GATEWAY → GET /bookings/{} — запрос данных бронирования пользователем ID={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("GATEWAY → GET /bookings?state={} — пользователь ID={} запрашивает свои бронирования", state, userId);
        return bookingClient.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("GATEWAY → GET /bookings/owner?state={} — владелец ID={} запрашивает бронирования своих вещей", state, ownerId);
        return bookingClient.getOwnerBookings(ownerId, state);
    }
}
