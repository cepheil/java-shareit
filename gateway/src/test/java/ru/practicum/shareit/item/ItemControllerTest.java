package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;


    @Test
    @DisplayName("POST /items — 400 при пустом названии")
    void createItem_shouldReturn400_whenNameBlank() throws Exception {
        ItemCreateDto invalid = new ItemCreateDto("", "описание", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.description").isNotEmpty());
    }


    @Test
    @DisplayName("PATCH /items/{id} — 400 при отрицательном ID")
    void updateItem_shouldReturn400_whenItemIdNegative() throws Exception {
        ItemUpdateDto dto = new ItemUpdateDto("Новая вещь", "новое описание", true, null);

        mockMvc.perform(patch("/items/{id}", -10)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ConstraintViolationException"));
    }


    @Test
    @DisplayName("GET /items/{id} — 400 при отрицательном ID")
    void getItem_shouldReturn400_whenIdNegative() throws Exception {
        mockMvc.perform(get("/items/{id}", -5)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ConstraintViolationException"));
    }


    @Test
    @DisplayName("POST /items/{id}/comment — 400 при пустом тексте")
    void addComment_shouldReturn400_whenTextBlank() throws Exception {
        CommentCreateDto invalid = new CommentCreateDto("");

        mockMvc.perform(post("/items/{id}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.description").isNotEmpty());
    }


    @Test
    @DisplayName("POST /items — 400 при отсутствии X-Sharer-User-Id")
    void createItem_shouldReturn400_whenNoHeader() throws Exception {
        ItemCreateDto valid = new ItemCreateDto("Название", "описание", true, null);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("MissingRequestHeaderException"))
                .andExpect(jsonPath("$.description").value(org.hamcrest.Matchers.containsString("X-Sharer-User-Id")));
    }
}