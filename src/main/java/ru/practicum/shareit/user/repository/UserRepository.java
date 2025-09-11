package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    boolean delete(Long id);

    Optional<User> findById(Long id);

    boolean existsByEmail(String email);

    boolean existsById(Long id);

}
