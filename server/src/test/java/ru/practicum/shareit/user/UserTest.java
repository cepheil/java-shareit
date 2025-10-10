package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void gettersSetters_toString_shouldWork() {
        User user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("ivan@mail.ru");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Ivan");
        assertThat(user.getEmail()).isEqualTo("ivan@mail.ru");

        String result = user.toString();
        assertThat(result).contains("User");
        assertThat(result).contains("ivan@mail.ru");
    }
}