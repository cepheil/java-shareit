package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;


@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "text", nullable = false, length = 1024)
    private String text;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_comment_item"))
    private Item item;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_comment_author"))
    private User author;


    @Override
    public String toString() {
        return "Comment(id=" + id + ", text=" + text + ")";
    }

}
