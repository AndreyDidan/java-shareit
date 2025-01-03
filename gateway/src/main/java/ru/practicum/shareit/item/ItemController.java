package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemsUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return itemClient.getAllItemsUser(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItem(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid CreateItemDto createItemDto) {
        return itemClient.createItem(userId, createItemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("id") Long itemId,
            @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("id") Long itemId,
            @RequestBody @Valid CreateCommentDto createCommentDto) {
        return itemClient.addComment(userId, itemId, createCommentDto);
    }
}