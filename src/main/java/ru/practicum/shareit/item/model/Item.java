package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 256, nullable = false)
    @NotBlank(message = "Название предмета не может быть пустым")
    private String name;

    @Column(name = "description", length = 512, nullable = false)
    @NotBlank(message = "Описание предмета не может быть пустым")
    private String description;

    @ManyToOne(optional = false)
    @NotNull(message = "Владелец обязателен")
    @JoinColumn(name = "owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_item_owner"))
    private User owner;

    @Column(nullable = false)
    @NotNull(message = "Статус доступности обязателен")
    private Boolean available;

    @ManyToOne(optional = true)
    @JoinColumn(name = "request_id",
            foreignKey = @ForeignKey(name = "fk_item_request"))
    private ItemRequest request;


}
