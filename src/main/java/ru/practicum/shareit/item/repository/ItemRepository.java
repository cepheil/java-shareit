package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Item create(Item item);

    Item update(Item item);

    Optional<Item> findById(Long itemId);

    Collection<Item> findByOwnerId(Long ownerId);

    Collection<Item> search(String text);

    boolean delete(Long id);
}
