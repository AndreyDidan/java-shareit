package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto createItem(Long userId, CreateItemDto createItemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = "
                + userId + " не найден!"));
        Item item = ItemMapper.mapToItem(createItemDto);
        item.setOwner(owner);
        itemRepository.createItem(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {

        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена!"));
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден!"));
        if (itemDto.getOwner() != null && !itemDto.getOwner().getId().equals(userId)) {
            throw new ValidationException("Вещь с id " + itemId + " не пренадлежит пользователю с id " + userId);
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        item = ItemMapper.mapToUpdatedItem(item, itemDto, itemId, owner);
        itemRepository.updateItem(itemId, item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        Item item = itemRepository.getItemById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id = "
                + itemId + " не найдена!"));
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = "
                + userId + " не найден!"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Вещь с id " + itemId + " не пренадлежит пользователю с id " + userId);
        }
        itemRepository.deleteItem(itemId);
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.getItemById(id).orElseThrow(() -> new NotFoundException("Вещь с id = " + id
                + " не найдена!"));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsUser(Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден!"));
        return itemRepository.getAllItemsUser(owner.getId()).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}