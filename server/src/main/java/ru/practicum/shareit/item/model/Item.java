package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;


@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", length = 512, nullable = false)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_item_owner"))
    private User owner;

    @Column(nullable = false)
    private Boolean available;

    @ManyToOne(optional = true)
    @JoinColumn(name = "request_id",
            foreignKey = @ForeignKey(name = "fk_item_request"))
    private ItemRequest request;

    @Override
    public String toString() {
        return "Item(id=" + id + ", name=" + name + ")";
    }

}
