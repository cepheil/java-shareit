package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;


    @PostMapping
    public BookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingCreateDto dto
    ) {
        log.info("POST /bookings - пользователь ID={} создает бронирование: {}", userId, dto);
        return bookingService.createBooking(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        log.info("PATCH /bookings/{}?approved={} - пользователь ID={} меняет статус",
                bookingId, approved, ownerId);
        return bookingService.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        log.info("GET /bookings/{} - пользователь ID={} запрашивает бронирование", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings?state={} - пользователь ID={} запрашивает список своих бронирований", state, userId);
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings/owner?state={} - владелец ID={} запрашивает бронирования своих вещей", state, ownerId);
        return bookingService.getOwnerBookings(ownerId, state);
    }


}
