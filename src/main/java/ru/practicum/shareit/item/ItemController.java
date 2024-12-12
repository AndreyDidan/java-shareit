package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItemsUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsUser(userId);
    }

    @GetMapping("/{id}")
    public ItemCommentDto getItemById(
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "text", defaultValue = "") String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid CreateItemDto createItemDto) {
        return itemService.createItem(userId, createItemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("id") Long itemId,
            @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{id}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("id") Long itemId,
            @RequestBody @Valid CreateCommentDto createCommentDto) {
        return itemService.addComment(userId, itemId, createCommentDto);
    }
}