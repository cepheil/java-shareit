package ru.practicum.shareit.item.mapper;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        String authorName = comment.getAuthor() != null ? comment.getAuthor().getName() : null;

        return new CommentDto(
                comment.getId(),
                comment.getText(),
                authorName,
                comment.getCreated()
        );
    }

    public static Comment toComment(CommentCreateDto dto, Item item, User author) {
        if (dto == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthor(author);

        return comment;
    }

}
