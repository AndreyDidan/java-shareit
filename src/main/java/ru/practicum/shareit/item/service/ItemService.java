package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto createItem(Long userId, CreateItemDto createItemDto);

    void deleteItem(Long userId, Long itemId);

    ItemCommentDto getItemById(Long id, Long userId);

    List<ItemDto> getAllItemsUser(Long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CreateCommentDto createCommentDto);

    //ItemCommentDto getItemById(Long userId, Long id);
}