package ru.practicum.shareit.item;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Valid @RequestBody ItemCreateDto dto
    ) {
        log.info("POST /items - пользователь {} создаёт вещь: {}", ownerId, dto.getName());
        return itemClient.createItem(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable @Positive Long itemId,
            @RequestBody ItemUpdateDto dto
    ) {
        log.info("PATCH /items/{} - пользователь {} обновляет вещь", itemId, ownerId);
        return itemClient.updateItem(ownerId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable @Positive Long itemId
    ) {
        log.info("GET /items/{} - запрос пользователем {}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        log.info("GET /items - список вещей владельца {}", ownerId);
        return itemClient.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("GET /items/search?text={} - поиск вещей", text);
        return itemClient.searchItems(text);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable @Positive Long itemId
    ) {
        log.info("DELETE /items/{} - удаление вещи пользователем {}", itemId, ownerId);
        return itemClient.deleteItem(ownerId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @PathVariable @Positive Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody CommentCreateDto dto
    ) {
        log.info("POST /items/{}/comment - пользователь {} добавляет комментарий", itemId, userId);
        return itemClient.createComment(userId, itemId, dto);
    }

}
