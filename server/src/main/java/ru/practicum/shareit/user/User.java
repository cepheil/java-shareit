package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;


    @Column(name = "email", nullable = false, unique = true, length = 512)
    private String email;

    @Override
    public String toString() {
        return "User(id=" + id + ", email=" + email + ")";
    }

}
