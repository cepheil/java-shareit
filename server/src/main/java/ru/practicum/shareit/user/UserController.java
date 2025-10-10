package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Validated
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping
    public UserDto createUser(@RequestBody UserCreateDto userCreateDto) {
        log.info("POST /users - запрос на создание нового пользователя {}", userCreateDto.getName());
        return userService.createUser(userCreateDto);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("GET /users - запрос на получения всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("GET /users/{userId} - запрос на получение пользователя c ID: {} ", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("DELETE /users/{userId} - запрос на удаление пользователя c ID: {} ", userId);
        userService.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateDto userUpdateDto) {
        log.info("PАTCH /users/{userId} - запрос на обновление данных пользователя c ID: {} ", userId);
        return userService.updateUser(userId, userUpdateDto);
    }


}
