package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Validated
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;


    @PostMapping
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestBody ItemCreateDto itemCreateDto
    ) {
        log.info("POST /items - добавление новой вещи пользователем ID={}", ownerId);
        return itemService.createItem(ownerId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemUpdateDto itemUpdateDto
    ) {
        log.info("PATCH /items/{} - обновление вещи пользователем ID={}", itemId, ownerId);
        return itemService.updateItem(ownerId, itemId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId
    ) {
        log.info("GET /items/{} - просмотр вещи пользователем ID={}", itemId, userId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemWithBookingsDto> getAllItemsByOwner(
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
            @PathVariable Long itemId
    ) {
        log.info("DELETE /items/{} - запрос на удаление вещи пользователем ID={}", itemId, ownerId);
        itemService.deleteItem(ownerId, itemId);
    }

    //POST /items/{itemId}/comment
    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody CommentCreateDto dto
    ) {
        log.info("POST /items/{itemId}/comment - добавление комментария к вещи ID={} пользователем ID={}", itemId, userId);
        return commentService.createComment(userId, itemId, dto);
    }


}
