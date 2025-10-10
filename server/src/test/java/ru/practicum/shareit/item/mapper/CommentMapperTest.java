package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    @DisplayName("toCommentDto — возвращает null при comment = null")
    void toCommentDto_shouldReturnNull_whenCommentNull() {
        CommentDto result = CommentMapper.toCommentDto(null);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toCommentDto — корректно маппит все поля при валидных данных")
    void toCommentDto_shouldMapAllFields() {
        User author = new User(1L, "Ivan", "ivan@mail.ru");
        Comment comment = new Comment(1L, "Хорошая вещь", LocalDateTime.now(), new Item(), author);

        CommentDto result = CommentMapper.toCommentDto(comment);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Хорошая вещь");
        assertThat(result.getAuthorName()).isEqualTo("Ivan");
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    @DisplayName("toCommentDto — author = null → authorName = null")
    void toCommentDto_shouldHandleNullAuthor() {
        Comment comment = new Comment(2L, "Без автора", LocalDateTime.now(), new Item(), null);

        CommentDto result = CommentMapper.toCommentDto(comment);

        assertThat(result.getAuthorName()).isNull();
        assertThat(result.getText()).isEqualTo("Без автора");
    }

    @Test
    @DisplayName("toComment — возвращает null при dto = null")
    void toComment_shouldReturnNull_whenDtoNull() {
        Comment result = CommentMapper.toComment(null, new Item(), new User());
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toComment — корректно маппит все поля при валидных данных")
    void toComment_shouldMapAllFields() {
        CommentCreateDto dto = new CommentCreateDto("Отличная вещь");
        Item item = new Item();
        item.setId(10L);
        User author = new User(3L, "Petr", "petr@mail.ru");

        Comment result = CommentMapper.toComment(dto, item, author);

        assertThat(result.getText()).isEqualTo("Отличная вещь");
        assertThat(result.getItem()).isEqualTo(item);
        assertThat(result.getAuthor()).isEqualTo(author);
        assertThat(result.getCreated()).isNotNull();
    }
}