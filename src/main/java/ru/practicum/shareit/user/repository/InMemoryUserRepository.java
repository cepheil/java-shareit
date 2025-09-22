//package ru.practicum.shareit.user.repository;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Repository;
//import ru.practicum.shareit.exception.NotFoundException;
//import ru.practicum.shareit.user.User;
//
//import java.util.*;
//import java.util.concurrent.atomic.AtomicLong;
//
//@Slf4j
//@Repository
//public class InMemoryUserRepository implements UserRepository {
//    private final Map<Long, User> storage = new HashMap<>();
//    private final AtomicLong idGenerator = new AtomicLong(0);
//
//
//    @Override
//    public User create(User user) {
//        long id = idGenerator.incrementAndGet();
//        user.setId(id);
//        storage.put(id, user);
//        log.debug("Создан пользователь ID={}", id);
//        return user;
//    }
//
//    @Override
//    public User update(User user) {
//        if (user.getId() == null || !storage.containsKey(user.getId())) {
//            throw new NotFoundException("Пользователь с ID=" + user.getId() + " не найден");
//        }
//        storage.put(user.getId(), user);
//        log.debug("Обновлён пользователь ID={}", user.getId());
//        return user;
//    }
//
//    @Override
//    public Collection<User> findAll() {
//        return new ArrayList<>(storage.values());
//    }
//
//    @Override
//    public boolean delete(Long id) {
//        return  storage.remove(id) != null;
//    }
//
//    @Override
//    public Optional<User> findById(Long id) {
//        return Optional.ofNullable(storage.get(id));
//    }
//
//    @Override
//    public boolean existsByEmail(String email) {
//        return storage.values().stream()
//                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
//    }
//
//    @Override
//    public boolean existsById(Long id) {
//        return storage.containsKey(id);
//    }
//}
