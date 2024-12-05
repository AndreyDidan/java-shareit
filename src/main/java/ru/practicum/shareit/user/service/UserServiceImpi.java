package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpi implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto findByIdUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден!"));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        User user = UserMapper.mapToUser(createUserDto);
        if (userRepository.findByEmail(createUserDto.getEmail()).isPresent()) {
            throw new DuplicatedException("Пользователь с таким email уже существует");
        }
        User newUser = userRepository.create(user);
        return UserMapper.mapToUserDto(newUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new DuplicatedException("Пользователь с таким email уже существует");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден!"));
        User updateUser = update(user, userDto);
        User userUpdate = userRepository.update(id, updateUser);
        return UserMapper.mapToUserDto(userUpdate);
    }

    @Override
    public void deleteUser(Long id) {
        User deleteUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден!"));
        userRepository.delete(deleteUser.getId());
    }

    private User update(User user, UserDto userDto) {
        if (userDto.getName() != null) {
            if (userDto.getName().isBlank()) {
                throw new ValidationException("Имя пользователя не может быть пустым");
            }
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            if (userDto.getEmail().isBlank()) {
                throw new ValidationException("Email пользователя не может быть пустым");
            }
            user.setEmail(userDto.getEmail());
        }
        return user;
    }
}