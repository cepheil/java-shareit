package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {

    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(max = 1024, message = "Комментарий не может быть длиннее 1024 символов")
    private String text;

}
