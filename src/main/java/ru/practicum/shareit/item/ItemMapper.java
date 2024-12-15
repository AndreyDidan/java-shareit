package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@NoArgsConstructor
public class ItemMapper {
    public static Item mapToItem(CreateItemDto createItemDto) {
        return Item.builder()
                .name(createItemDto.getName())
                .description(createItemDto.getDescription())
                .available(createItemDto.getAvailable())
                .request(createItemDto.getRequest() != null ? ItemRequestMapper
                        .toItemRequest(createItemDto.getRequest()) : null)
                .build();
    }

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner() != null ? item.getOwner() : null)
                .request(item.getRequest() != null ? ItemRequestMapper.toItemRequestDto(item.getRequest()) : null)
                .build();
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
        allDto.setRequest(item.getRequest() != null ? ItemRequestMapper.toItemRequestDto(item.getRequest()) : null);
        allDto.setLastBooking(lastBooking);
        allDto.setNextBooking(nextBooking);
        allDto.setComments(comments);
        return allDto;
    }
}