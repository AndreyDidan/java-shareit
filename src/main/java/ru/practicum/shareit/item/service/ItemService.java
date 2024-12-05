package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto createItem(Long userId, CreateItemDto createItemDto);

    void deleteItem(Long userId, Long itemId);

    ItemDto getItemById(Long id);

    List<ItemDto> getAllItemsUser(Long userId);

    List<ItemDto> search(String text);
}