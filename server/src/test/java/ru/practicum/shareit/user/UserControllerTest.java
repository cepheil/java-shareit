package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("POST /users — создание нового пользователя")
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserCreateDto createDto = new UserCreateDto("Ivan", "ivan@mail.ru");
        UserDto responseDto = new UserDto(1L, "Ivan", "ivan@mail.ru");

        when(userService.createUser(any(UserCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Ivan")))
                .andExpect(jsonPath("$.email", is("ivan@mail.ru")));

        verify(userService, times(1)).createUser(any(UserCreateDto.class));
    }


    @Test
    @DisplayName("GET /users — получение всех пользователей")
    void getAllUsers_shouldReturnList() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "Ivan", "ivan@mail.ru"),
                new UserDto(2L, "Oleg", "oleg@mail.ru")
        );
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Ivan")))
                .andExpect(jsonPath("$[1].email", is("oleg@mail.ru")));

        verify(userService, times(1)).getAllUsers();
    }


    @Test
    @DisplayName("GET /users/{id} — получение пользователя по ID")
    void getUserById_shouldReturnUser() throws Exception {
        UserDto user = new UserDto(1L, "Ivan", "ivan@mail.ru");
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Ivan")))
                .andExpect(jsonPath("$.email", is("ivan@mail.ru")));

        verify(userService).getUserById(1L);
    }


    @Test
    @DisplayName("DELETE /users/{id} — удаление пользователя")
    void deleteUser_shouldCallServiceAndReturnOk() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }


    @Test
    @DisplayName("PATCH /users/{id} — обновление пользователя")
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto("Updated", "updated@mail.ru");
        UserDto updatedDto = new UserDto(1L, "Updated", "updated@mail.ru");

        when(userService.updateUser(eq(1L), any(UserUpdateDto.class))).thenReturn(updatedDto);

        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated")))
                .andExpect(jsonPath("$.email", is("updated@mail.ru")));

        verify(userService).updateUser(eq(1L), any(UserUpdateDto.class));
    }


}