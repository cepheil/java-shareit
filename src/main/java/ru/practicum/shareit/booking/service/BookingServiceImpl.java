package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;


    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingCreateDto dto) {
        return null;
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long ownerId, Long bookingId, boolean approved) {
        return null;
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        return null;
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        return List.of();
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        return List.of();
    }
}
