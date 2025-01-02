package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceImpiTest {

    @Autowired
    UserService userService;

    @Autowired
    private EntityManager entityManager;
    private CreateUserDto createUserDto;

    @BeforeEach
    void setUp() {
        createUserDto = new CreateUserDto("Andrey", "andrey@mail.ru");
    }

    @Test
    void findByIdUser() {
        Long id = userService.createUser(createUserDto).getId();
        UserDto userById = userService.findByIdUser(id);
        assertThat(userById.getId(), equalTo(id));
        assertThat(userById.getName(), equalTo(createUserDto.getName()));
        assertThat(userById.getEmail(), equalTo(createUserDto.getEmail()));
    }

    @Test
    void createUser() {
        UserDto userDto = userService.createUser(createUserDto);
        assertNotNull(userDto);
        assertThat(userDto.getName(), equalTo(createUserDto.getName()));
        assertThat(userDto.getEmail(), equalTo(createUserDto.getEmail()));
    }

    @Test
    void updateUser() {
        Long id = userService.createUser(createUserDto).getId();
        UserDto newUserDto = new UserDto(null, "newandrey@mail.it", "newAndrey");
        userService.updateUser(id, newUserDto);
        UserDto userById = userService.findByIdUser(id);

        assertThat(userById.getId(), equalTo(id));
        assertThat(userById.getName(), equalTo(newUserDto.getName()));
        assertThat(userById.getEmail(), equalTo(newUserDto.getEmail()));
    }

    @Test
    void deleteUser() {
        Long id = userService.createUser(createUserDto).getId();
        userService.deleteUser(id);
        assertThrows(NotFoundException.class, () -> userService.findByIdUser(id));
    }
}