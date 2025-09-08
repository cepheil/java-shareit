package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;


public interface UserService {

    UserDto createUser(UserCreateDto userCreateDto);

    Collection<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    void deleteUser(Long userId);

    UserDto updateUser(Long userId, UserUpdateDto userUpdateDto);
}
