package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpi implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item createItem(Item item) {
        Long itemId = id++;
        item.setId(itemId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена!");
        }
        Item updateItem = items.get(itemId);
        item.setId(itemId);
        item.setOwner(updateItem.getOwner());
        items.put(itemId, item);
        return item;
    }

    @Override
    public void deleteItem(Long id) {
        items.remove(id);
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        if (items.containsKey(id)) {
            return Optional.of(items.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Item> getAllItemsUser(Long userId) {
        return items.values().stream().filter(item -> item.getOwner().getId().equals(userId)).toList();
    }

    @Override
    public List<Item> search(String text) {
        if (items.values().isEmpty()) {
            return List.of();
        }

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }
}