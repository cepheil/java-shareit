package ru.practicum.shareit.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    @Size(min = 1, max = 255, message = "Имя должно содержать хотя бы 1 символ")
    private String name;
    @Email(message = "Некорректный формат email")
    private String email;
}
