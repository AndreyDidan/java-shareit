package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInItemDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestInItemDto create(Long userId, CreateItemRequestDto createItemRequestDto);

    List<ItemRequestInItemDto> getUserRequests(Long userId);

    List<ItemRequestInItemDto> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestInItemDto getRequestById(Long requestId, Long userId);
}
