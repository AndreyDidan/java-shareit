package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto findByIdUser(Long id);

    UserDto createUser(CreateUserDto createUserDto);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}