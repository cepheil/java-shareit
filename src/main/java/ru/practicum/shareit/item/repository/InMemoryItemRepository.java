//package ru.practicum.shareit.item.repository;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Repository;
//import ru.practicum.shareit.exception.NotFoundException;
//import ru.practicum.shareit.item.model.Item;
//
//import java.util.*;
//import java.util.concurrent.atomic.AtomicLong;
//
//@Slf4j
//@Repository
//public class InMemoryItemRepository implements ItemRepository {
//    private final Map<Long, Item> storage = new HashMap<>();
//    private final AtomicLong idGenerator = new AtomicLong(0);
//
//
//    @Override
//    public Item create(Item item) {
//        long id = idGenerator.incrementAndGet();
//        item.setId(id);
//        storage.put(id, item);
//        log.debug("Пользователем ID: {} создана вещь ID={}", item.getOwnerId(), id);
//        return item;
//    }
//
//    @Override
//    public Item update(Item item) {
//        if (item.getId() == null || !storage.containsKey(item.getId())) {
//            throw new NotFoundException("Вещь с ID=" + item.getId() + " не найдена");
//        }
//        storage.put(item.getId(), item);
//        log.debug("Обновлена вещь ID={}", item.getId());
//        return item;
//    }
//
//    @Override
//    public Optional<Item> findById(Long itemId) {
//        return Optional.ofNullable(storage.get(itemId));
//    }
//
//    @Override
//    public Collection<Item> findByOwnerId(Long ownerId) {
//        return storage.values().stream()
//                .filter(item -> item.getOwnerId().equals(ownerId))
//                .toList();
//    }
//
//    @Override
//    public Collection<Item> search(String text) {
//        String lower = text.toLowerCase();
//        return storage.values().stream()
//                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
//                .filter(item -> item.getName().toLowerCase().contains(lower)
//                                || item.getDescription().toLowerCase().contains(lower))
//                .toList();
//    }
//
//    @Override
//    public boolean delete(Long id) {
//        return storage.remove(id) != null;
//    }
//}
