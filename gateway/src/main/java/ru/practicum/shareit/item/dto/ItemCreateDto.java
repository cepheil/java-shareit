package ru.practicum.shareit.item.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateDto {

    @NotBlank(message = "Название предмета не может быть пустым")
    @Size(max = 255, message = "Имя не может быть длиннее 255 символов")
    private String name;

    @NotBlank(message = "Описание предмета не может быть пустым")
    @Size(max = 512, message = "Описание не может быть длиннее 512 символов")
    private String description;

    @NotNull(message = "Статус доступности обязателен")
    private Boolean available;

    private Long requestId;

}
