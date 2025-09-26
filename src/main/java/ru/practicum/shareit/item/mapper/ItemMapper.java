package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item, List<Comment> comments) {
        if (item == null) {
            return null;
        }

        List<CommentDto> commentDtos = (comments != null)
                ? comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .toList()
                : List.of();

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                commentDtos
        );
    }


    public static ItemWithBookingsDto toItemWithBookingsDto(
            Item item,
            Booking lastBooking,
            Booking nextBooking,
            List<Comment> comments
    ) {
        if (item == null) {
            return null;
        }

        List<CommentDto> commentDtos = (comments != null)
                ? comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .toList()
                : List.of();

        BookingShortDto lastBookingDto = (lastBooking != null)
                ? new BookingShortDto(lastBooking.getId(), lastBooking.getBooker().getId())
                : null;

        BookingShortDto nextBookingDto = (nextBooking != null)
                ? new BookingShortDto(nextBooking.getId(), nextBooking.getBooker().getId())
                : null;

        return new ItemWithBookingsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBookingDto,
                nextBookingDto,
                commentDtos
        );
    }


    public static Item toItem(ItemCreateDto dto, User owner, ItemRequest request) {
        if (dto == null) {
            return null;
        }
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setRequest(request);
        item.setOwner(owner);
        return item;
    }


    public static void updateItem(Item item, ItemUpdateDto dto, ItemRequest request) {
        if (item == null || dto == null) {
            return;
        }
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        if (dto.getRequestId() != null) {
            item.setRequest(request);
        }
    }


}
