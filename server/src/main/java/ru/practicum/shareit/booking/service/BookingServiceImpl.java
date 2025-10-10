package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
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
        if (userId == null) {
            log.error("ID пользователя не может быть null");
            throw new ConflictException("ID пользователя не может быть null");
        }

        if (dto.getStart() == null || dto.getEnd() == null) {
            log.error("Дата начала и окончания обязательны");
            throw new ConflictException("Дата начала и окончания обязательны");
        }

        if (!dto.getStart().isBefore(dto.getEnd())) {
            log.error("Дата начала должна быть раньше даты окончания");
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }

        if (dto.getStart().isBefore(LocalDateTime.now())) {
            log.error("Дата начала не может быть в прошлом");
            throw new ConflictException("Дата начала не может быть в прошлом");
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь ID=" + userId + " не найден"));

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь ID=" + dto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            log.error("Вещь ID={} недоступна для бронирования", item.getId());
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (booker.getId().equals(item.getOwner().getId())) {
            log.error("Пользователь ID={} пытается забронировать свою вещь ID={}", booker.getId(), item.getId());
            throw new ForbiddenException("Нельзя забронировать свою вещь");
        }

        Booking booking = BookingMapper.toBooking(dto, item, booker);
        booking.setStatus(Status.WAITING);
        Booking created = bookingRepository.save(booking);
        log.info("Пользователь ID={}, создал бронирование вещи ID={}", userId, item.getId());
        return BookingMapper.toBookingDto(created);
    }


    @Override
    @Transactional
    public BookingDto updateBooking(Long ownerId, Long bookingId, boolean approved) {
        if (ownerId == null || bookingId == null) {
            log.error("ID владельца ={} и ID бронирования ={} не может быть null", ownerId, bookingId);
            throw new ConflictException("ID владельца и ID бронирования не может быть null");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование ID=" + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            log.error("Только владелец может может подтверждать/отклонять бронирование");
            throw new ForbiddenException("Только владелец может может подтверждать/отклонять бронирование");
        }

        if (booking.getStatus() == Status.APPROVED) {
            log.warn("Бронирование уже имеет статус {} ", booking.getStatus());
            throw new ConflictException("Бронирование уже подтверждено");
        }

        if (approved) {
            log.info("Владелец ID={}, подтвердил бронирование вещи ID={}", ownerId, booking.getItem().getId());
            booking.setStatus(Status.APPROVED);
        } else {
            log.info("Владелец ID={}, отклонил бронирование вещи ID={}", ownerId, booking.getItem().getId());
            booking.setStatus(Status.REJECTED);
        }

        Booking updated = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(updated);
    }


    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        if (userId == null || bookingId == null) {
            log.error("ID пользователя ={} и ID бронирования ={} не может быть null", userId, bookingId);
            throw new ConflictException("ID пользователя и ID бронирования не может быть null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя ID=" + userId + " не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование ID=" + bookingId + " не найдено"));

        if (!booking.getBooker().getId().equals(userId) &&
            !booking.getItem().getOwner().getId().equals(userId)) {
            log.error("Только владелец вещи или автор бронирования может получать данные о бронировании");
            throw new ForbiddenException("Только владелец вещи или автор бронирования может получать данные о бронировании");
        }
        log.info("Пользователь ID={} запросил данные о бронировании ID={}", userId, bookingId);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        if (userId == null) {
            log.error("ID пользователя не может быть null");
            throw new ConflictException("ID пользователя не может быть null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя ID=" + userId + " не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            case "ALL":
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);

        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }


    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        if (ownerId == null) {
            log.error("ID владельца не может быть null");
            throw new ConflictException("ID владельца не может быть null");
        }
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь ID=" + ownerId + " не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
                break;
            case "PAST":
                bookings = bookingRepository
                        .findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
                break;
            case "WAITING":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED);
                break;
            case "ALL":
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }
}
