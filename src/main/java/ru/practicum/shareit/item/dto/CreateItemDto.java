package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateItemDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private ItemRequestDto request;
}