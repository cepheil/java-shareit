package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Validated
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Valid @RequestBody ItemCreateDto itemCreateDto
    ) {
        log.info("POST /items - добавление новой вещи пользователем ID={}", ownerId);
        return itemService.createItem(ownerId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable @Positive Long itemId,
            @RequestBody ItemUpdateDto itemUpdateDto
    ) {
        log.info("PATCH /items/{} - обновление вещи пользователем ID={}", itemId, ownerId);
        return itemService.updateItem(ownerId, itemId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable @Positive Long itemId
    ) {
        log.info("GET /items/{} - просмотр вещи пользователем ID={}", itemId, userId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("GET /items - получение списка вещей владельца ID={}", ownerId);
        return itemService.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(
            @RequestParam String text
    ) {
        log.info("GET /items/search?text={} - поиск доступных вещей", text);
        return itemService.searchItems(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable @Positive Long itemId
    ) {
        log.info("DELETE /items/{} - запрос на удаление вещи пользователем ID={}", itemId, ownerId);
        itemService.deleteItem(ownerId, itemId);
    }


}
