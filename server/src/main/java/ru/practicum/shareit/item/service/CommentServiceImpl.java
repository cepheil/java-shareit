package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;


    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long itemId, CommentCreateDto dto) {
        if (userId == null || itemId == null) {
            log.error("ID пользователя={} или ID вещи={} не может быть null", userId, itemId);
            throw new ConflictException("ID пользователя и ID вещи не могут быть null");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь ID=" + userId + " не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещь с ID=" + itemId + " не найдена"));

        boolean hasBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, Status.APPROVED, LocalDateTime.now()
        );

        if (!hasBooking) {
            log.error("Пользователь ID={} не может оставить комментарий к вещи ID={} — нет завершённого бронирования",
                    userId, itemId);
            throw new ValidationException("Комментарий можно оставить только после завершения бронирования");
        }

        Comment comment = CommentMapper.toComment(dto, item, author);
        Comment created = commentRepository.save(comment);

        log.info("Пользователь ID={} оставил комментарий ID={} к вещи ID={}", userId, created.getId(), itemId);
        return CommentMapper.toCommentDto(created);
    }

    @Override
    public List<CommentDto> getCommentsByItem(Long itemId) {
        if (itemId == null) {
            log.error("ID вещи не может быть null");
            throw new ConflictException("ID вещи не может быть null");
        }

        List<Comment> comments = commentRepository.findByItemId(itemId);
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }
}
