package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false, length = 1024)
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;

    @Column(name = "created", nullable = false)
    @NotNull(message = "Дата создания комментария обязательна")
    private LocalDateTime created;

    @ManyToOne(optional = false)
    @NotNull(message = "предмет комментария обязателен")
    @JoinColumn(name = "item_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_comment_item"))
    private Item item;

    @ManyToOne(optional = false)
    @NotNull(message = "автор комментария обязателен")
    @JoinColumn(name = "author_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_comment_author"))
    private User author;

}
