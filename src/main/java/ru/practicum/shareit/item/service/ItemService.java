package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(Long ownerId, ItemCreateDto itemCreateDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto);

    ItemDto getItemById(Long userId, Long itemId);

    Collection<ItemDto> getAllItemsByOwner(Long ownerId);

    Collection<ItemDto> searchItems(String text);

    void deleteItem(Long ownerId, Long itemId);

}
