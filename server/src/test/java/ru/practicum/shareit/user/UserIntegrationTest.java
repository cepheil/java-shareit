package ru.practicum.shareit.user;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
//@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Создание и получение пользователя из БД")
    void createAndFindUser() {
        UserCreateDto dto = new UserCreateDto("Ivan", "ivan@mail.ru");

        UserDto created = userService.createUser(dto);
        assertThat(created.getId()).isNotNull();

        UserDto found = userService.getUserById(created.getId());
        assertThat(found.getName()).isEqualTo("Ivan");
        assertThat(found.getEmail()).isEqualTo("ivan@mail.ru");
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsers() {
        userService.createUser(new UserCreateDto("User1", "u1@mail.ru"));
        userService.createUser(new UserCreateDto("User2", "u2@mail.ru"));

        Collection<UserDto> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserDto::getEmail)
                .containsExactlyInAnyOrder("u1@mail.ru", "u2@mail.ru");
    }


    @Test
    @DisplayName("Обновление пользователя в БД")
    void updateUser() {
        UserDto created = userService.createUser(new UserCreateDto("Old", "old@mail.ru"));
        UserUpdateDto updateDto = new UserUpdateDto("NewName", "new@mail.ru");

        UserDto updated = userService.updateUser(created.getId(), updateDto);

        assertThat(updated.getName()).isEqualTo("NewName");
        assertThat(updated.getEmail()).isEqualTo("new@mail.ru");

        UserDto found = userService.getUserById(created.getId());
        assertThat(found.getEmail()).isEqualTo("new@mail.ru");
    }


    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser() {
        UserDto created = userService.createUser(new UserCreateDto("ToDelete", "delete@mail.ru"));
        Long id = created.getId();

        userService.deleteUser(id);

        assertThrows(NotFoundException.class, () -> userService.getUserById(id));
        assertThat(userRepository.existsById(id)).isFalse();
    }

    @Test
    @DisplayName("Ошибка при создании — email уже существует")
    void shouldThrowConflictWhenEmailExists() {
        userService.createUser(new UserCreateDto("User1", "same@mail.ru"));

        assertThrows(ConflictException.class,
                () -> userService.createUser(new UserCreateDto("Another", "same@mail.ru")));
    }


    @Test
    @DisplayName("Ошибка при обновлении несуществующего пользователя")
    void shouldThrowNotFoundWhenUpdatingUnknownUser() {
        assertThrows(NotFoundException.class,
                () -> userService.updateUser(999L, new UserUpdateDto("X", "x@mail.ru")));
    }


}
