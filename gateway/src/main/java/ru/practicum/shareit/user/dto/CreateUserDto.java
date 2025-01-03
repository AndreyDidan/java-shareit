package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class CreateUserDto {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}