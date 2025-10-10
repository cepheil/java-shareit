package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    // POST /requests — создать новый запрос
    @PostMapping
    public ItemRequestDto createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestCreateDto dto) {
        log.info("POST /requests - пользователь ID={} создаёт запрос", userId);
        return requestService.createRequest(userId, dto);
    }

    // GET /requests — получить все запросы пользователя
    @GetMapping
    public List<ItemRequestDto> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests - запрос всех запросов пользователя ID={}", userId);
        return requestService.getUserRequests(userId);
    }

    // GET /requests/all?from=0&size=10 — получить все запросы других пользователей
    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /requests/all - пользователь ID={} получает все запросы, from={}, size={}", userId, from, size);
        return requestService.getAllRequests(userId, from, size);
    }

    // GET /requests/{requestId} — получить запрос по ID
    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        log.info("GET /requests/{} - пользователь ID={} получает запрос", requestId, userId);
        return requestService.getRequestById(userId, requestId);
    }


}
