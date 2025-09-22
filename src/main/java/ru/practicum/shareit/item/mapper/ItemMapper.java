package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }

        Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;
        Long requestId = item.getRequest() != null ? item.getRequest().getId() : null;

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                ownerId,
                item.getAvailable(),
                requestId
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
