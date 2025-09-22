package ru.practicum.shareit.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 256, nullable = false)
    @NotNull(message = "Имя не может быть пустым")
    private String name;

    @NotNull(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    @Column(name = "email", nullable = false, unique = true, length = 512)
    private String email;

}
