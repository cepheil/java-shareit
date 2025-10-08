package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingRequestHeaderException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;


    @Test
    @DisplayName("POST /items — успешное создание вещи")
    void createItem_shouldReturnCreatedItem() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto("Item", "Item description", true, null);
        ItemDto responseDto = new ItemDto(1L, "Item", "Item description", true, null, List.of());

        when(itemService.createItem(eq(1L), any(ItemCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Item")))
                .andExpect(jsonPath("$.description", is("Item description")))
                .andExpect(jsonPath("$.available", is(true)));

        verify(itemService).createItem(eq(1L), any(ItemCreateDto.class));
    }


    @Test
    @DisplayName("PATCH /items/{id} — успешное обновление вещи")
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        ItemUpdateDto updateDto = new ItemUpdateDto("Updated", "Changed", false, null);
        ItemDto updated = new ItemDto(1L, "Updated", "Changed", false, null, List.of());

        when(itemService.updateItem(eq(1L), eq(1L), any(ItemUpdateDto.class))).thenReturn(updated);

        mockMvc.perform(patch("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated")))
                .andExpect(jsonPath("$.description", is("Changed")))
                .andExpect(jsonPath("$.available", is(false)));

        verify(itemService).updateItem(eq(1L), eq(1L), any(ItemUpdateDto.class));
    }


    @Test
    @DisplayName("GET /items/{id} — получение вещи по ID")
    void getItemById_shouldReturnItem() throws Exception {
        ItemWithBookingsDto dto = new ItemWithBookingsDto(1L, "Item", "Item description", true, null, null, null, List.of());

        when(itemService.getItemById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Item")))
                .andExpect(jsonPath("$.description", is("Item description")));

        verify(itemService).getItemById(1L, 1L);
    }


    @Test
    @DisplayName("GET /items — получение всех вещей владельца")
    void getAllItemsByOwner_shouldReturnList() throws Exception {
        List<ItemWithBookingsDto> items = List.of(
                new ItemWithBookingsDto(1L, "Item1", "Desc1", true, null, null, null, List.of()),
                new ItemWithBookingsDto(2L, "Item2", "Desc2", true, null, null, null, List.of())
        );

        when(itemService.getAllItemsByOwner(1L)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Item1")))
                .andExpect(jsonPath("$[1].name", is("Item2")));

        verify(itemService).getAllItemsByOwner(1L);
    }


    @Test
    @DisplayName("GET /items/search?text=drill — успешный поиск")
    void searchItems_shouldReturnResults() throws Exception {
        List<ItemDto> items = List.of(new ItemDto(1L, "Item", "Desc", true, null, List.of()));

        when(itemService.searchItems("item")).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Item")));

        verify(itemService).searchItems("item");
    }


    @Test
    @DisplayName("DELETE /items/{id} — успешное удаление")
    void deleteItem_shouldReturnOk() throws Exception {
        doNothing().when(itemService).deleteItem(1L, 1L);

        mockMvc.perform(delete("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(1L, 1L);
    }


    @Test
    @DisplayName("POST /items/{id}/comment — успешное добавление комментария")
    void createComment_shouldReturnComment() throws Exception {
        CommentCreateDto createDto = new CommentCreateDto("Great item!");
        CommentDto responseDto = new CommentDto(1L, "Great item!", "Ivan", null);

        when(commentService.createComment(eq(1L), eq(1L), any(CommentCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items/{id}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Great item!")))
                .andExpect(jsonPath("$.authorName", is("Ivan")));

        verify(commentService).createComment(eq(1L), eq(1L), any(CommentCreateDto.class));
    }


    @Nested
    @DisplayName("Проверка обязательных заголовков и параметров")
    class ValidationTests {

        @Test
        @DisplayName("GET /items/{id} — выбрасывает MissingRequestHeaderException при отсутствии X-Sharer-User-Id")
        void getItemById_shouldThrowMissingHeaderException() throws Exception {
            mockMvc.perform(get("/items/{id}", 1))
                    .andExpect(result -> {
                        assertThat(result.getResolvedException())
                                .isInstanceOf(MissingRequestHeaderException.class)
                                .hasMessageContaining("X-Sharer-User-Id");
                    });
        }

        @Test
        @DisplayName("DELETE /items/{id} — выбрасывает MissingRequestHeaderException при отсутствии X-Sharer-User-Id")
        void deleteItem_shouldThrowMissingHeaderException() throws Exception {
            mockMvc.perform(delete("/items/{id}", 1))
                    .andExpect(result -> {
                        assertThat(result.getResolvedException())
                                .isInstanceOf(MissingRequestHeaderException.class)
                                .hasMessageContaining("X-Sharer-User-Id");
                    });
        }

        @Test
        @DisplayName("POST /items/{id}/comment — выбрасывает MissingRequestHeaderException при отсутствии X-Sharer-User-Id")
        void createComment_shouldThrowMissingHeaderException() throws Exception {
            CommentCreateDto dto = new CommentCreateDto("text");

            mockMvc.perform(post("/items/{id}/comment", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(result -> {
                        assertThat(result.getResolvedException())
                                .isInstanceOf(MissingRequestHeaderException.class)
                                .hasMessageContaining("X-Sharer-User-Id");
                    });
        }
    }


}