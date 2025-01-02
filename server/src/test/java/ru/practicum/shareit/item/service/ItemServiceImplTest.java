package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsRequestDto;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
class ItemServiceImplTest {
    @Autowired
    ItemService itemService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    BookingService bookingService;

    private CreateItemDto createItemDto;
    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        createItemDto = new CreateItemDto("testItem", "testDescription", true, List.of(), null);
        userDto = userService.createUser(new CreateUserDto("andrey", "andrey@mail.ru"));
        itemDto = new ItemDto(1L, "item", "item descr", true, null, userDto.getId());
    }

    @Test
    void createItem() {
        ItemsRequestDto itemsRequestDto = itemService.createItem(userDto.getId(), createItemDto);
        assertThat(itemsRequestDto.getName(), equalTo(createItemDto.getName()));
        assertThat(itemsRequestDto.getDescription(), equalTo(createItemDto.getDescription()));
    }

    @Test
    void updateItemNotFound() {
        User user = new User(1L, "testName", "test@mail.ru");
        ItemDto itemDto = new ItemDto(1L, "testItem1", "testDescription1", true, user,
                null);
        Assertions.assertThrows(NotFoundException.class, () -> itemService.updateItem(userDto.getId(), 1L, itemDto));
    }

    @Test
    void updateItemUserNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            ItemDto itemDto = new ItemDto(1L, "testItem1", "testDescription1", true,
                    null,null);
            itemService.updateItem(1L, 1L, itemDto);
        });
    }

    @Test
    void getItemById() {
        ItemsRequestDto itemsRequestDto = itemService.createItem(userDto.getId(), createItemDto);
        ItemCommentDto item = itemService.getItemById(itemsRequestDto.getId(), userDto.getId());
        assertThat(item.getId(), equalTo(itemsRequestDto.getId()));
    }

    @Test
    void getItemNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItemById(userDto.getId(), 1L));
    }

    @Test
    void getItemUserNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L));
    }

    @Test
    void getAllItemsUser() {
        ItemsRequestDto itemsRequestDto = itemService.createItem(userDto.getId(), createItemDto);
        assertThat(itemService.getAllItemsUser(userDto.getId()), equalTo(List.of(itemsRequestDto)));
    }

    @Test
    void search() {
        ItemsRequestDto itemsRequestDto = itemService.createItem(userDto.getId(), createItemDto);
        assertThat(itemService.search("test"), equalTo(List.of(itemsRequestDto)));
    }
}