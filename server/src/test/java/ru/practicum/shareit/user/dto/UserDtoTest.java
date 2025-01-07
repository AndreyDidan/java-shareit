package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws IOException {
        UserDto userDto = new UserDto(1L, "Andrey", "andrey@mail.ru");
        JsonContent<UserDto> jsonContent = json.write(userDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(userDto.getId()));
        assertThat(jsonContent).extractingJsonPathStringValue("$.name")
                .satisfies(name -> assertThat(name).isEqualTo(userDto.getName()));
        assertThat(jsonContent).extractingJsonPathStringValue("$.email")
                .satisfies(email -> assertThat(email).isEqualTo(userDto.getEmail()));
    }
}