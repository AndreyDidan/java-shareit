package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
class ItemRequestServiceImplTest {
    @Autowired
    ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;
    private CreateItemRequestDto createItemRequestDto;
    private UserDto userDto;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        userDto = userService.createUser(new CreateUserDto("Andrey", "andrey@mail.ru"));
        createItemRequestDto = new CreateItemRequestDto("testRequestDescription");
    }

    @Test
    void create() {
        Long id = itemRequestService.create(userDto.getId(), createItemRequestDto).getId();

        TypedQuery<ItemRequest> query = entityManager.createQuery(
                "select request from ItemRequest request where request.id = :id", ItemRequest.class);
        ItemRequest request = query.setParameter("id", id).getSingleResult();
        assertThat(request.getId(), equalTo(id));
        assertThat(request.getDescription(), equalTo(createItemRequestDto.getDescription()));
        assertThat(request.getRequestor().getId(), equalTo(userDto.getId()));
        assertThat(request.getRequestor().getId(), equalTo(userDto.getId()));
    }

    @Test
    void getUserRequests() {
        Long id = itemRequestService.create(userDto.getId(), createItemRequestDto).getId();

        List<ItemRequestInItemDto> requestDto1 = itemRequestService.getUserRequests(userDto.getId());
        assertThat(requestDto1.size(), equalTo(1));
        assertThat(requestDto1.getFirst().getId(), equalTo(id));
        assertThat(requestDto1.getFirst().getDescription(), equalTo(createItemRequestDto.getDescription()));
        assertThat(requestDto1.getFirst().getRequestor().getId(), equalTo(userDto.getId()));
    }

    @Test
    void getAllRequests() {
        Long id = itemRequestService.create(userDto.getId(), createItemRequestDto).getId();

        List<ItemRequestInItemDto> requestDto1 = itemRequestService.getUserRequests(userDto.getId());

        assertThat(requestDto1.size(), equalTo(1));
        assertThat(requestDto1.getFirst().getId(), equalTo(id));
        assertThat(requestDto1.getFirst().getDescription(), equalTo(createItemRequestDto.getDescription()));
        assertThat(requestDto1.getFirst().getRequestor().getId(), equalTo(userDto.getId()));
    }

    @Test
    void getRequestById() {
        Long id = itemRequestService.create(userDto.getId(), createItemRequestDto).getId();

        ItemRequestInItemDto requestDtoS = itemRequestService.getRequestById(id, userDto.getId());
        assertThat(requestDtoS.getId(), equalTo(id));
        assertThat(requestDtoS.getDescription(), equalTo(createItemRequestDto.getDescription()));
        assertThat(requestDtoS.getRequestor().getId(), equalTo(userDto.getId()));
    }
}