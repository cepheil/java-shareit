package ru.practicum.shareit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShareItAppTest {

    @Test
    void contextLoads() {
        ShareItApp.main(new String[]{});
        assertThat(true).isTrue();
    }
}