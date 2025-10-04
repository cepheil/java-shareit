package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDto {
    @Size(min = 1, max = 255, message = "Название должно содержать хотя бы 1 символ и не быть длиннее 255")
    private String name;

    @Size(min = 1, max = 512, message = "Описание должно содержать хотя бы 1 символ и не быть длиннее 512")
    private String description;

    private Boolean available;

    private Long requestId;

}
