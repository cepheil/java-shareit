package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public ItemDto createItem(Long ownerId, ItemCreateDto itemCreateDto) {
        if (ownerId == null) {
            throw new ConflictException("ID владельца не может быть null");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Владелец ID=" + ownerId + " не найден"));

        ItemRequest request = null;
        if (itemCreateDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemCreateDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос ID=" + itemCreateDto.getRequestId() + " не найден"));
        }

        Item item = ItemMapper.toItem(itemCreateDto, owner, request);
        Item created = itemRepository.save(item);
        List<Comment> comments = commentRepository.findByItemId(created.getId());

        log.info("Пользователем ID={},  создана новая вещь ID={}", ownerId, created.getId());
        return ItemMapper.toItemDto(created, comments);
    }


    @Override
    @Transactional
    public ItemDto updateItem(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto) {
        if (ownerId == null || itemId == null) {
            log.error("Попытка обновления с некорректными ID (ownerId={}, itemId={})", ownerId, itemId);
            throw new ConflictException("ID владельца и ID вещи не могут быть null");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещь с ID=" + itemId + " не найдена"));

        if (!item.getOwner().getId().equals(ownerId)) {
            log.error("Пользователь ID={} попытался обновить чужую вещь ID={}", ownerId, itemId);
            throw new ForbiddenException("Редактировать вещь может только её владелец");
        }

        ItemRequest request = null;
        if (itemUpdateDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemUpdateDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос ID=" + itemUpdateDto.getRequestId() + " не найден"));
        }

        ItemMapper.updateItem(item, itemUpdateDto, request);
        Item updated = itemRepository.save(item);
        List<Comment> comments = commentRepository.findByItemId(updated.getId());

        log.info("Пользователь ID={} обновил вещь ID={}", ownerId, updated.getId());
        return ItemMapper.toItemDto(updated, comments);
    }


    @Override
    public ItemWithBookingsDto getItemById(Long userId, Long itemId) {
        if (itemId == null || userId == null) {
            log.error("ID пользователя и ID вещи не могут быть null");
            throw new ConflictException("ID пользователя и ID вещи не могут быть null");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещь с ID=" + itemId + " не найдена"));

        LocalDateTime now = LocalDateTime.now();

        List<Comment> comments = commentRepository.findByItemId(itemId);
        Booking lastBooking = bookingRepository
                .findLastBooking(itemId, now, Status.APPROVED, PageRequest.of(0, 1))
                .stream().findFirst().orElse(null);

        Booking nextBooking = bookingRepository
                .findNextBooking(itemId, now, Status.APPROVED, PageRequest.of(0, 1))
                .stream().findFirst().orElse(null);

        if (item.getOwner().getId().equals(userId)) {
            log.info("Хозяин вещи ID={} запросил вещь ID={}", userId, itemId);
            return ItemMapper.toItemWithBookingsDto(item,
                    lastBooking,
                    nextBooking,
                    comments
            );

        }
        log.info("Пользователь ID={} запросил вещь ID={}", userId, itemId);
        return ItemMapper.toItemWithBookingsDto(item, null, null, comments);

    }


    @Override
    public Collection<ItemWithBookingsDto> getAllItemsByOwner(Long ownerId) {
        if (ownerId == null) {
            log.error("ID владельца не может быть null, запрос отклонён");
            throw new ConflictException("ID владельца не может быть null");
        }
        if (!userRepository.existsById(ownerId)) {
            log.error("Владелец ID={}, не найден", ownerId);
            throw new NotFoundException("Владелец ID=" + ownerId + " не найден");
        }

        Collection<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(ownerId);
        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items
                .stream()
                .map(Item::getId)
                .toList();

        Map<Long, List<Comment>> commentsByItem = commentRepository.findByItemIds(itemIds)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        Map<Long, Booking> lastBookings = bookingRepository.findLastBookings(itemIds, now).stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (b1, b2) -> b1 // берём первый, т.к. сортировка DESC
                ));

        Map<Long, Booking> nextBookings = bookingRepository.findNextBookings(itemIds, now).stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (b1, b2) -> b1 // берём первый, т.к. сортировка ASC
                ));

        log.info("Пользователь ID={} запросил список своих вещей ({} шт.)", ownerId, items.size());
        return items.stream()
                .map(item -> ItemMapper.toItemWithBookingsDto(
                        item,
                        lastBookings.get(item.getId()),
                        nextBookings.get(item.getId()),
                        commentsByItem.getOrDefault(item.getId(), List.of())
                ))
                .toList();
    }


    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            log.info("Поисковый запрос пустой → возвращаем пустой список");
            return List.of();
        }

        Collection<Item> found = itemRepository.search(text);
        if (found.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = found.stream().map(Item::getId).toList();

        Map<Long, List<Comment>> commentsByItem = commentRepository.findByItemIds(itemIds).stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        log.info("Поиск по тексту='{}' вернул {} вещей", text, found.size());
        return found.stream()
                .map(item -> ItemMapper.toItemDto(item,
                        commentsByItem.getOrDefault(item.getId(), List.of())
                )).toList();
    }


    @Override
    @Transactional
    public void deleteItem(Long ownerId, Long itemId) {
        if (ownerId == null || itemId == null) {
            log.error("Попытка удаления с некорректными ID (ownerId={}, itemId={})", ownerId, itemId);
            throw new ConflictException("ID владельца и ID вещи не могут быть null");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь ID=" + itemId + " не найдена"));

        if (!item.getOwner().getId().equals(ownerId)) {
            log.error("Пользователь ID={} попытался удалить чужую вещь ID={}", ownerId, itemId);
            throw new ForbiddenException("Удалить вещь может только её владелец");
        }

        itemRepository.deleteById(itemId);
        log.info("Пользователь ID={} удалил вещь ID={}", ownerId, itemId);
    }
}
