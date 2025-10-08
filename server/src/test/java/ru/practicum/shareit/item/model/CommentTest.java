package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    void gettersSetters_toString_shouldWork() {
        User author = new User(2L, "Oleg", "oleg@mail.ru");
        Item item = new Item();
        item.setId(7L);
        item.setName("Вещь для комментария");

        Comment comment = new Comment();
        comment.setId(3L);
        comment.setText("Отличная вещь!");
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthor(author);

        assertThat(comment.getId()).isEqualTo(3L);
        assertThat(comment.getText()).isEqualTo("Отличная вещь!");
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getCreated()).isNotNull();

        String result = comment.toString();
        assertThat(result).contains("Comment");
        assertThat(result).contains("Отличная вещь");
    }
}