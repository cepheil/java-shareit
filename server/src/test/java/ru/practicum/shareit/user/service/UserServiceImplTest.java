package ru.practicum.shareit.user.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Ivan", "ivan@mail.ru");
        userDto = new UserDto(1L, "Ivan", "ivan@mail.ru");
    }


    @Test
    @DisplayName("Создание пользователя — успешный сценарий")
    void createUser_shouldCreateUserSuccessfully() {
        UserCreateDto createDto = new UserCreateDto("Ivan", "ivan@mail.ru");

        when(userMapper.toUser(createDto)).thenReturn(user);
        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(createDto);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).save(user);
    }


    @Test
    @DisplayName("Создание пользователя — выбрасывает ConflictException при дубликате email")
    void createUser_shouldThrowConflictWhenEmailExists() {
        UserCreateDto createDto = new UserCreateDto("Ivan", "ivan@mail.ru");
        when(userMapper.toUser(createDto)).thenReturn(user);
        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(createDto));

        verify(userRepository, never()).save(any());
    }


    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        List<UserDto> result = (List<UserDto>) userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("ivan@mail.ru");
    }


    @Test
    @DisplayName("Получение пользователя по ID — успешный сценарий")
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(1L);

        assertThat(result.getName()).isEqualTo("Ivan");
    }


    @Test
    @DisplayName("Получение пользователя по ID — выбрасывает ValidationException, если ID null")
    void getUserById_shouldThrowValidationWhenIdIsNull() {
        assertThrows(ValidationException.class, () -> userService.getUserById(null));
    }


    @Test
    @DisplayName("Получение пользователя по ID — выбрасывает NotFoundException, если не найден")
    void getUserById_shouldThrowNotFoundWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }


    @Test
    @DisplayName("Удаление пользователя — успешный сценарий")
    void deleteUser_shouldDeleteUserSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Удаление пользователя — выбрасывает ValidationException, если ID null")
    void deleteUser_shouldThrowValidationWhenIdIsNull() {
        assertThrows(ValidationException.class, () -> userService.deleteUser(null));
    }


    @Test
    @DisplayName("Удаление пользователя — выбрасывает NotFoundException, если не найден")
    void deleteUser_shouldThrowNotFoundWhenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    @DisplayName("Обновление пользователя — успешный сценарий")
    void updateUser_shouldUpdateUserSuccessfully() {
        UserUpdateDto updateDto = new UserUpdateDto("Updated", "updated@example.com");
        User updated = new User(1L, "Updated", "updated@example.com");
        UserDto updatedDto = new UserDto(1L, "Updated", "updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        doAnswer(invocation -> {
            user.setName(updateDto.getName());
            user.setEmail(updateDto.getEmail());
            return null;
        }).when(userMapper).updateUser(user, updateDto);
        when(userRepository.save(user)).thenReturn(updated);
        when(userMapper.toUserDto(updated)).thenReturn(updatedDto);

        UserDto result = userService.updateUser(1L, updateDto);

        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Обновление пользователя — выбрасывает ConflictException при занятом email")
    void updateUser_shouldThrowConflictWhenEmailExists() {
        UserUpdateDto updateDto = new UserUpdateDto("Updated", "used@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("used@example.com")).thenReturn(true);
        user.setEmail("old@example.com");

        assertThrows(ConflictException.class, () -> userService.updateUser(1L, updateDto));
    }

    @Test
    @DisplayName("Обновление пользователя — выбрасывает NotFoundException, если не найден")
    void updateUser_shouldThrowNotFoundWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.updateUser(1L, new UserUpdateDto("New", "new@example.com")));
    }




}