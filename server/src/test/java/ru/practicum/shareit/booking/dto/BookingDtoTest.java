package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemsRequestDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Andrey");
        user.setEmail("andrey@gmail.com");

        ItemsRequestDto itemsRequestDto = new ItemsRequestDto(1L, "testName", "testDescription",
                true, List.of(), null);

        BookingDto booking = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(25))
                .booker(UserMapper.mapToUserDto(user))
                .item(itemsRequestDto)
                .status(Status.APPROVED)
                .build();

        JsonContent<BookingDto> result = json.write(booking);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.start")
                .hasJsonPath("$.end")
                .hasJsonPath("$.status")
                .hasJsonPath("$.booker.id")
                .hasJsonPath("$.booker.name")
                .hasJsonPath("$.booker.email")
                .hasJsonPath("$.item.id")
                .hasJsonPath("$.item.available")
                .hasJsonPath("$.item.description")
                .hasJsonPath("$.item.name");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(item_id -> assertThat(item_id.longValue()).isEqualTo(booking.getId()));
        assertThat(result).extractingJsonPathStringValue("$.start")
                .satisfies(created -> assertThat(created).isNotNull());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .satisfies(created -> assertThat(created).isNotNull());
        assertThat(result).extractingJsonPathValue("$.status")
                .satisfies(status -> assertThat(status).isEqualTo(booking.getStatus().name()));

        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .satisfies(item_id -> assertThat(item_id.longValue()).isEqualTo(booking.getBooker().getId()));
        assertThat(result).extractingJsonPathStringValue("$.booker.name")
                .satisfies(item_description -> assertThat(item_description).isEqualTo(booking.getBooker().getName()));
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .satisfies(item_description -> assertThat(item_description).isEqualTo(booking.getBooker().getEmail()));

        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(booking.getItem().getId()));

        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .satisfies(item_name -> assertThat(item_name).isEqualTo(booking.getItem().getName()));
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                .satisfies(item_description -> assertThat(item_description).isEqualTo(booking.getItem().getDescription()));
        assertThat(result).extractingJsonPathBooleanValue("$.item.available")
                .satisfies(item_available -> assertThat(item_available).isEqualTo(booking.getItem().getAvailable()));
    }
}