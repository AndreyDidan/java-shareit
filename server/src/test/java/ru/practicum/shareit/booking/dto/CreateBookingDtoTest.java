package ru.practicum.shareit.booking.dto;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemsRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CreateBookingDtoTest {
    @Autowired
    private JacksonTester<CreateBookingDto> json;

    @Test
    void testSerialize() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Andrey");
        user.setEmail("andrey@gmail.com");

        ItemsRequestDto itemsRequestDto = new ItemsRequestDto(1L, "testName", "testDescription",
                true, List.of(), null);
        CreateBookingDto createBookingDto = new CreateBookingDto(1L, LocalDateTime.now(),
                LocalDateTime.now().plusHours(1));
        JsonContent<CreateBookingDto> result = json.write(createBookingDto);

        assertThat(result).hasJsonPath("$.itemId")
                .hasJsonPath("$.start")
                .hasJsonPath("$.end");

        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .satisfies(item_id -> AssertionsForClassTypes.assertThat(item_id.longValue()).isEqualTo(createBookingDto.getItemId()));
        assertThat(result).extractingJsonPathStringValue("$.start")
                .satisfies(created -> AssertionsForClassTypes.assertThat(created).isNotNull());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .satisfies(created -> AssertionsForClassTypes.assertThat(created).isNotNull());
    }
}