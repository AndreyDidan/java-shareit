package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestInItemDtoTest {
    @Autowired
    private JacksonTester<ItemRequestInItemDto> json;

    @Test
    @SneakyThrows
    public void testSerialize() throws IOException {
        User user = new User(1L, "Andrey", "andrey@mail.ru");
        ItemRequestInItemDto itemRequestInItemDto = new ItemRequestInItemDto();
        itemRequestInItemDto.setId(1L);
        itemRequestInItemDto.setDescription("testDescription");
        itemRequestInItemDto.setRequestor(user);
        itemRequestInItemDto.setCreated(LocalDateTime.now());

        JsonContent<ItemRequestInItemDto> content = json.write(itemRequestInItemDto);

        assertThat(content)
                .hasJsonPath("$.id")
                .hasJsonPath("$.description")
                .hasJsonPath("$.requestor")
                .hasJsonPath("$.created");
        assertThat(content).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("@.description").isEqualTo("testDescription");
        assertThat(content).extractingJsonPathStringValue("@.requestor.name").isEqualTo("Andrey");
    }
}