package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public ItemDto createItem(Long ownerId, ItemCreateDto itemCreateDto) {
        if (ownerId == null) {
            throw new ValidationException("ID владельца не может быть null");
        }
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Владелец ID=" + ownerId + " не найден");
        }
        Item item = ItemMapper.toItem(itemCreateDto, ownerId);
        Item created = itemRepository.create(item);
        log.info("Пользователем ID={},  создана новая вещь ID={}", ownerId, created.getId());
        return ItemMapper.toItemDto(created);
    }


    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto) {
        if (ownerId == null || itemId == null) {
            log.error("Попытка обновления с некорректными ID (ownerId={}, itemId={})", ownerId, itemId);
            throw new ValidationException("ID владельца и ID вещи не могут быть null");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещь с ID=" + itemId + " не найдена"));

        if (!item.getOwnerId().equals(ownerId)) {
            log.error("Пользователь ID={} попытался обновить чужую вещь ID={}", ownerId, itemId);
            throw new ForbiddenException("Редактировать вещь может только её владелец");
        }
        ItemMapper.updateItem(item, itemUpdateDto);
        Item updated = itemRepository.update(item);
        log.info("Пользователь ID={} обновил вещь ID={}", ownerId, updated.getId());
        return ItemMapper.toItemDto(updated);
    }


    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        if (itemId == null) {
            log.error("Запрос вещи с null-ID отклонён");
            throw new ValidationException("ID вещи не может быть null");
        }
        if (userId == null) {
            log.error("ID пользователя не может быть null, запрос отклонён");
            throw new ValidationException("ID пользователя не может быть null");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещь с ID=" + itemId + " не найдена"));

        log.info("Пользователь ID={} запросил вещь ID={}", userId, itemId);
        return ItemMapper.toItemDto(item);
    }


    @Override
    public Collection<ItemDto> getAllItemsByOwner(Long ownerId) {
        if (ownerId == null) {
            log.error("ID владельца не может быть null, запрос отклонён");
            throw new ValidationException("ID владельца не может быть null");
        }
        if (!userRepository.existsById(ownerId)) {
            log.error("Владелец ID={}, не найден", ownerId);
            throw new NotFoundException("Владелец ID=" + ownerId + " не найден");
        }

        Collection<Item> items = itemRepository.findByOwnerId(ownerId);
        log.info("Пользователь ID={} запросил список своих вещей ({} шт.)", ownerId, items.size());

        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }


    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            log.info("Поисковый запрос пустой → возвращаем пустой список");
            return List.of();
        }

        Collection<Item> found = itemRepository.search(text);
        log.info("Поиск по тексту='{}' вернул {} вещей", text, found.size());

        return found.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }


    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        if (ownerId == null || itemId == null) {
            log.error("Попытка удаления с некорректными ID (ownerId={}, itemId={})", ownerId, itemId);
            throw new ValidationException("ID владельца и ID вещи не могут быть null");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь ID=" + itemId + " не найдена"));

        if (!item.getOwnerId().equals(ownerId)) {
            log.error("Пользователь ID={} попытался удалить чужую вещь ID={}", ownerId, itemId);
            throw new ValidationException("Удалить вещь может только её владелец");
        }

        itemRepository.delete(itemId);
        log.info("Пользователь ID={} удалил вещь ID={}", ownerId, itemId);
    }
}
