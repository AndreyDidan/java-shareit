package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsRequestDto;

import java.util.List;

public interface ItemService {
    ItemsRequestDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemsRequestDto createItem(Long userId, CreateItemDto createItemDto);

    void deleteItem(Long userId, Long itemId);

    ItemCommentDto getItemById(Long id, Long userId);

    List<ItemsRequestDto> getAllItemsUser(Long userId);

    List<ItemsRequestDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CreateCommentDto createCommentDto);
}