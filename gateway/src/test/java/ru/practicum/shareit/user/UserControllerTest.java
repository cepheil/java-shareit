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


import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;


    @Test
    @DisplayName("POST /users — 400 при пустом имени")
    void createUser_shouldReturn400_whenNameBlank() throws Exception {
        UserCreateDto invalid = new UserCreateDto("", "user@mail.ru");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.description").isNotEmpty());
    }

    @Test
    @DisplayName("POST /users — 400 при некорректном email")
    void createUser_shouldReturn400_whenEmailInvalid() throws Exception {
        UserCreateDto invalid = new UserCreateDto("Ivan", "wrong-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.description").value(containsString("Некорректный формат email")));
    }


    @Test
    @DisplayName("GET /users/{id} — 400 при отрицательном ID")
    void getUser_shouldReturn400_whenIdNegative() throws Exception {
        mockMvc.perform(get("/users/{id}", -1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ConstraintViolationException"))
                .andExpect(jsonPath("$.description").value(containsString("must be greater than 0")));
    }


    @Test
    @DisplayName("DELETE /users/{id} — 400 при отрицательном ID")
    void deleteUser_shouldReturn400_whenIdNegative() throws Exception {
        mockMvc.perform(delete("/users/{id}", -10))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ConstraintViolationException"));
    }


}