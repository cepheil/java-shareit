package ru.practicum.shareit.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    @DisplayName("Проверка сериализации UserDto в JSON")
    void testSerialize() throws Exception {
        UserDto dto = new UserDto(1L, "Ivan", "ivan@mail.ru");

        var result = json.write(dto);

        assertThat(result).hasJsonPathValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("ivan@mail.ru");
    }

    @Test
    @DisplayName("Проверка десериализации JSON в UserDto")
    void testDeserialize() throws Exception {
        String content = """
                {
                  "id": 1,
                  "name": "Ivan",
                  "email": "ivan@mail.ru"
                }
                """;

        UserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Ivan");
        assertThat(dto.getEmail()).isEqualTo("ivan@mail.ru");
    }


}
