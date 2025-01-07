package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@NoArgsConstructor
public class ItemMapper {
    public static Item mapToItem(CreateItemDto createItemDto) {
        if (createItemDto.getName() == null || createItemDto.getName().isBlank()) {
            throw new ValidationException("Поле \"name\" обязательно для заполнения!");
        }
        return Item.builder()
                .name(createItemDto.getName())
                .description(createItemDto.getDescription())
                .available(createItemDto.getAvailable())
                .build();
    }

    public static Item mapToItem(CreateItemDto createItemDto, ItemRequest request) {
        Item item = new Item();
        item.setName(createItemDto.getName());
        item.setDescription(createItemDto.getDescription());
        item.setAvailable(createItemDto.getAvailable());
        item.setRequest(request);
        return item;
    }

    public static ItemsRequestDto mapToItemDto(Item item) {
        ItemsRequestDto itemsRequestDto = new ItemsRequestDto();
        itemsRequestDto.setId(item.getId());
        itemsRequestDto.setName(item.getName());
        itemsRequestDto.setDescription(item.getDescription());
        itemsRequestDto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            itemsRequestDto.setRequestId(item.getRequest().getId());
        }
        return itemsRequestDto;
    }

    public static Item mapToUpdatedItem(Item currentItem, ItemDto updatedFields, Long itemId, User owner) {
        currentItem.setId(itemId);
        currentItem.setOwner(owner);

        if (updatedFields.getName() != null) {
            currentItem.setName(updatedFields.getName());
        }
        if (updatedFields.getDescription() != null) {
            currentItem.setDescription(updatedFields.getDescription());
        }
        if (updatedFields.getAvailable() != null) {
            currentItem.setAvailable(updatedFields.getAvailable());
        }
        return currentItem;
    }

    public static ItemCommentDto mapToItemAllDto(Item item, BookingDto lastBooking,
                                                 BookingDto nextBooking, List<CommentDto> comments) {
        ItemCommentDto allDto = new ItemCommentDto();
        allDto.setId(item.getId());
        allDto.setName(item.getName());
        allDto.setDescription(item.getDescription());
        allDto.setAvailable(item.getAvailable());
        allDto.setOwner(item.getOwner());
        if (item.getRequest() != null) {
            allDto.setRequestId(item.getRequest().getId());
        }
        allDto.setLastBooking(lastBooking);
        allDto.setNextBooking(nextBooking);
        allDto.setComments(comments);
        return allDto;
    }
}