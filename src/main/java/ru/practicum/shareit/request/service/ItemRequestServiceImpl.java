package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, ItemRequestCreateDto dto) {
        if (userId == null) {
            log.error("ID пользователя не может быть null");
            throw new ValidationException("ID пользователя не может быть null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя ID=" + userId + " не найден"));

        ItemRequest request = ItemRequestMapper.toItemRequest(dto, user);
        ItemRequest created = requestRepository.save(request);

        log.info("Пользователем ID={},  создана запрос ID={}", userId, created.getId());
        return ItemRequestMapper.toItemRequestDto(created, Collections.emptyList());
    }


    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        if (userId == null) {
            log.error("ID пользователя не может быть null");
            throw new ValidationException("ID пользователя не может быть null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь ID=" + userId + " не найден"));

        List<ItemRequest> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> allItems = itemRepository.findByRequestIdIn(requestIds);

        Map<Long, List<ItemDto>> itemsByRequestId = allItems
                .stream()
                .collect(Collectors.groupingBy(
                        i -> i.getRequest().getId(),
                        Collectors.mapping(item -> ItemMapper.toItemDto(item, List.of()), Collectors.toList())
                ));
        return requests
                .stream()
                .map(r -> ItemRequestMapper.toItemRequestDto(r, itemsByRequestId.getOrDefault(r.getId(), List.of())))
                .toList();
    }


    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        if (userId == null || requestId == null) {
            log.error("ID пользователя={} или ID запроса={} не может быть null", userId, requestId);
            throw new ValidationException("ID пользователя и ID запроса не могут быть null");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь ID=" + userId + " не найден"));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос ID=" + requestId + " не найден"));

        List<Item> items = itemRepository.findByRequestIdIn(List.of(requestId));

        List<ItemDto> itemDtos = items.stream()
                .map(item -> ItemMapper.toItemDto(item, List.of()))
                .toList();

        log.info("Пользователь ID={} запросил данные о запросе ID={}", userId, requestId);
        return ItemRequestMapper.toItemRequestDto(request, itemDtos);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        if (userId == null) {
            log.error("ID пользователя не может быть null");
            throw new ValidationException("ID пользователя не может быть null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь ID=" + userId + " не найден"));

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<ItemRequest> requests = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, page);

        if (requests.isEmpty()) {
            return List.of();
        }
        List<Long> requestIds = requests
                .stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> allItems = itemRepository.findByRequestIdIn(requestIds);

        Map<Long, List<ItemDto>> itemsByRequestId = allItems.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getRequest().getId(),
                        Collectors.mapping(item -> ItemMapper.toItemDto(item, List.of()), Collectors.toList())
                ));

        return requests
                .stream()
                .map(r -> ItemRequestMapper.toItemRequestDto(r, itemsByRequestId.getOrDefault(r.getId(), List.of())))
                .toList();
    }
}
