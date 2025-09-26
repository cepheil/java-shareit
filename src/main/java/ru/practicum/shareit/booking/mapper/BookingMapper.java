package ru.practicum.shareit.booking.mapper;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookerDto bookerDto = booking.getBooker() != null
                ? new BookerDto(booking.getBooker().getId())
                : null;

        ItemShortDto itemShortDto = booking.getItem() != null
                ? new ItemShortDto(booking.getItem().getId(), booking.getItem().getName())
                : null;

        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                bookerDto,
                itemShortDto
        );
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingShortDto(
                booking.getId(),
                booking.getBooker() != null ? booking.getBooker().getId() : null
        );
    }


    public static Booking toBooking(BookingCreateDto dto, Item item, User booker) {
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
