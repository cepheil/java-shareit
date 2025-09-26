package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;


@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Дата начала бронирования обязательна")
    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    @NotNull(message = "Дата окончания бронирования обязательна")
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_booking_item"))
    @NotNull(message = "Предмет обязателен")
    private Item item;

    @ManyToOne(optional = false)
    @JoinColumn(name = "booker_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_booking_booker"))
    @NotNull(message = "Арендатор обязателен")
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @NotNull(message = "Статус обязателен")
    private Status status;

    @Override
    public String toString() {
        return "Booking(id=" + id + ", status=" + status + ")";
    }

}
