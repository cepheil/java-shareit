package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;


    private ItemRequestDto redRequest;
    private ItemRequestDto greenRequest;

    @BeforeEach
    void setUp() {
        redRequest = new ItemRequestDto(
                1L,
                "Требуется красная вещь",
                LocalDateTime.of(2025, 10, 8, 10, 0),
                List.of(new ItemDto(1L, "Красная вещь", "Описание", true, null, List.of()))
        );

        greenRequest = new ItemRequestDto(
                2L,
                "Требуется зеленая вещь",
                LocalDateTime.of(2025, 10, 7, 12, 0),
                List.of(new ItemDto(2L, "Зеленая вещь", "Описание", true, null, List.of()))
        );
    }

    @Test
    @DisplayName("POST /requests — успешное создание запроса")
    void createRequest_shouldReturnOk() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Требуется тестовая вещь");
        ItemRequestDto response = new ItemRequestDto(
                10L, "Требуется тестовая вещь", LocalDateTime.now(), List.of()
        );

        when(requestService.createRequest(eq(1L), any(ItemRequestCreateDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)  // Иван
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Требуется тестовая вещь"));
    }

    @Test
    @DisplayName("GET /requests — получение всех запросов пользователя (Иван)")
    void getUserRequests_shouldReturnList() throws Exception {
        when(requestService.getUserRequests(1L))
                .thenReturn(List.of(redRequest, greenRequest));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Требуется красная вещь"))
                .andExpect(jsonPath("$[1].description").value("Требуется зеленая вещь"));
    }


    @Test
    @DisplayName("GET /requests/all — получение всех запросов других пользователей (Олег)")
    void getAllRequests_shouldReturnList() throws Exception {
        when(requestService.getAllRequests(eq(2L), anyInt(), anyInt()))
                .thenReturn(List.of(redRequest, greenRequest));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Требуется красная вещь"))
                .andExpect(jsonPath("$[1].description").value("Требуется зеленая вещь"));
    }


    @Test
    @DisplayName("GET /requests/{id} — получение запроса по ID")
    void getRequestById_shouldReturnRequest() throws Exception {
        when(requestService.getRequestById(1L, 1L))
                .thenReturn(redRequest);

        mockMvc.perform(get("/requests/{id}", 1)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Требуется красная вещь"));
    }


}