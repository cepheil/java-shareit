package ru.practicum.shareit.request;

import jakarta.persistence.*;
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
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_request_user"))
    private User requester;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Override
    public String toString() {
        return "ItemRequest(id=" + id + ", description=" + description + ")";
    }

}
