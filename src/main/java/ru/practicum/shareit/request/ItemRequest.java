package ru.practicum.shareit.request;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;


@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "description", nullable = false, length = 512)
    @NotBlank(message = "Описание предмета не может быть пустым")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_request_user"))
    @NotNull(message = "Автор запроса обязателен")
    private User requester;

    @Column(name = "created", nullable = false)
    @NotNull(message = "Дата запроса обязательна")
    private LocalDateTime created;

    @Override
    public String toString() {
        return "ItemRequest(id=" + id + ", description=" + description + ")";
    }

}
