package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(Long ownerId, ItemCreateDto itemCreateDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto);

    ItemWithBookingsDto getItemById(Long userId, Long itemId);

    Collection<ItemWithBookingsDto> getAllItemsByOwner(Long ownerId);

    Collection<ItemDto> searchItems(String text);

    void deleteItem(Long ownerId, Long itemId);

}
