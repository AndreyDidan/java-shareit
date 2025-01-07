package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInItemDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping("/{requestId}")
    public ItemRequestInItemDto findById(@PathVariable Long requestId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }

    @PostMapping
    public ItemRequestInItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestBody CreateItemRequestDto createItemRequestDto
    ) {
        return itemRequestService.create(userId, createItemRequestDto);
    }

    @GetMapping
    public List<ItemRequestInItemDto> findByAuthor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestInItemDto> findAll(@RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequests(userId, from, size);
    }
}