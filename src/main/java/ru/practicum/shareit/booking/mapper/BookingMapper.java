package ru.practicum.shareit.booking.mapper;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        Long bookerId = booking.getBooker() != null ? booking.getBooker().getId() : null;
        Long itemId = booking.getItem() != null ? booking.getItem().getId() : null;

        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                bookerId,
                itemId
        );
    }

    public static Booking toBooking (BookingCreateDto dto, Item item, User booker) {
        if (dto == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        return booking;
    }

}
