package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(CreateItemRequestDto createItemRequestDto, User author) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(author);
        itemRequest.setDescription(createItemRequestDto.getDescription());
        return itemRequest;

    }

    public static ItemRequestInItemDto toItemRequestInItemDto(ItemRequest itemRequest) {
        return ItemRequestInItemDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(itemRequest.getRequestor())
                .build();
    }
}