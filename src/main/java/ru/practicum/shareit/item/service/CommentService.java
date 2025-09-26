package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(Long userId, Long itemId, CommentCreateDto dto);

    List<CommentDto> getCommentsByItem(Long itemId);

}
