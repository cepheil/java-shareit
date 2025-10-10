package ru.practicum.shareit.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Slf4j
@Validated
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    // POST /requests
    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemRequestCreateDto dto
    ) {
        log.info("POST /requests - пользователь {} создаёт запрос", userId);
        return itemRequestClient.createRequest(userId, dto);
    }

    // GET /requests
    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET /requests - пользователь {} запрашивает свои запросы", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    // GET /requests/all
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("GET /requests/all - пользователь {} получает все запросы, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    // GET /requests/{requestId}
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable @Positive long requestId
    ) {
        log.info("GET /requests/{} - пользователь {} запрашивает конкретный запрос", requestId, userId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

}
