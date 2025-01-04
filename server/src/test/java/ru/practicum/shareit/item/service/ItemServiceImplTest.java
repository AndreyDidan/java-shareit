package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsRequestDto;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
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
        createItemDto = new CreateItemDto("testItem", "testDescription", true, null);
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
    void createItemValidationException() {
        CreateItemDto createItemDto1 = new CreateItemDto(null, "testDescription", true, null);
        Assertions.assertThrows(ValidationException.class, () -> itemService.createItem(userDto.getId(), createItemDto1));
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

    @Test
    void updateItem() {
        UserDto userDto = userService.createUser(new CreateUserDto("test", "test@test.com"));
        ItemsRequestDto itemsRequestDto = itemService.createItem(userDto.getId(), createItemDto);
        User user = new User(userDto.getId(), userDto.getName(), userDto.getEmail());
        ItemDto itemDto1 = new ItemDto(itemsRequestDto.getId(), "newName", "NewDescription",
                false, user, null);
        ItemsRequestDto itemsRequestDtoUpdate = itemService.updateItem(userDto.getId(), itemsRequestDto.getId(), itemDto1);
        assertThat(itemsRequestDtoUpdate.getName(), equalTo("newName"));
        assertThat(itemsRequestDtoUpdate.getDescription(), equalTo("NewDescription"));
        assertThat(itemsRequestDtoUpdate.getAvailable(), equalTo(false));
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
    void addComment() {
        UserDto userDto1 = userService.createUser(new CreateUserDto("test", "test@test.com"));
        UserDto userDto2 = userService.createUser(new CreateUserDto("test2", "test2@test.com"));
        ItemsRequestDto itemsRequestDto = itemService.createItem(userDto1.getId(), createItemDto);
        BookingDto bookingDto = bookingService.create(userDto2.getId(),
                new CreateBookingDto(itemsRequestDto.getId(),
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now()));
        bookingService.update(userDto1.getId(), bookingDto.getId(), true);
        CreateCommentDto createCommentDto = new CreateCommentDto("testComment");
        CommentDto commentDto = itemService.addComment(userDto2.getId(), itemsRequestDto.getId(), createCommentDto);
        assertThat(commentDto.getText(), equalTo("testComment"));
        assertThat(commentDto.getAuthorName(), equalTo("test2"));
    }
}