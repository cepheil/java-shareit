package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto (ItemRequest request, List<ItemDto> items) {
        if (request == null) {
            return null;
        }
        List<ItemDto> safeItems = items == null ? Collections.emptyList() : new ArrayList<>(items);
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                safeItems
        );
    }

    public static ItemRequest toItemRequest(ItemRequestCreateDto dto, User requester) {
        if (dto == null) {
            return null;
        }
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        return request;
    }

}
