package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        dto.setOwnerId(item.getOwner().getId());

        dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        dto.setBookingCount(item.getBookingCount() !=null ? item.getBookingCount() : null);

        return dto;
    }

    public static Item toItem (ItemDto itemDto, User owner, ItemRequest request) {
        if (itemDto == null) {
            return null;
        }
        if (owner == null) {
            throw new ValidationException("Owner cannot be null for item creation");
        }

        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(owner);
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : false);
        item.setRequest(request);

        return item;
    }




}
