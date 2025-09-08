package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        User user = UserMapper.toUser(userCreateDto);
        if (userRepository.existsByEmail(user.getEmail())) {
            log.error("Email уже используется: {}", user.getEmail());
            throw new ConflictException("Email уже используется: " + user.getEmail());
        }

        User created = userRepository.create(user);
        log.info("Создан новый пользователь ID={} email={}", created.getId(), created.getEmail());

        return UserMapper.toUserDto(created);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        if (userId == null) {
            log.error("Запрос пользователя с null-ID отклонён");
            throw new ValidationException("ID пользователя не может быть null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID=" + userId + " не найден"));

        log.info("Получен пользователь ID={}", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (userId == null) {
            log.error("Удаление пользователя с null-ID отклонён");
            throw new ValidationException("ID пользователя не может быть null");
        }
        if (!userRepository.existsById(userId)) {
            log.warn("Пользователь с ID={} не найден при попытке удаления", userId);
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден");
        }
        userRepository.delete(userId);
        log.info("Удалён пользователь ID={}", userId);
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь ID=" + userId + " не найден"));

        if (userUpdateDto.getEmail() != null &&
            userRepository.existsByEmail(userUpdateDto.getEmail()) &&
            !userUpdateDto.getEmail().equals(user.getEmail())) {
            log.error("Email уже используется: {}", userUpdateDto.getEmail());
            throw new ConflictException("Email уже используется: " + userUpdateDto.getEmail());
        }

        UserMapper.updateUser(user, userUpdateDto);
        User updated = userRepository.update(user);
        log.info("Обновлён пользователь ID={}", updated.getId());

        return UserMapper.toUserDto(updated);
    }
}
