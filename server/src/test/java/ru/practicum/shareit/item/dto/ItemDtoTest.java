package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Name");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(null);
        item.setRequestId(1L);

        JsonContent<ItemDto> result = json.write(item);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.requestId")
                .hasJsonPath("$.description")
                .hasJsonPath("$.available")
                .hasJsonPath("$.name");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(item.getId()));
        assertThat(result).extractingJsonPathStringValue("$.name")
                .satisfies(item_name -> assertThat(item_name).isEqualTo(item.getName()));
        assertThat(result).extractingJsonPathStringValue("$.description")
                .satisfies(item_description -> assertThat(item_description).isEqualTo(item.getDescription()));
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .satisfies(item_available -> assertThat(item_available).isEqualTo(item.getAvailable()));
    }
}