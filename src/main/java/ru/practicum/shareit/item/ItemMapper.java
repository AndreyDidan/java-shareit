package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.User;

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
        currentItem.setId(itemId); // ID остаётся прежним
        currentItem.setOwner(owner); // Владельца устанавливаем из `owner` переданного в сервисе

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
}