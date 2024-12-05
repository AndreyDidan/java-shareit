package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item createItem(Item item);

    Item updateItem(Long itemId, Item item);

    void deleteItem(Long id);

    Optional<Item> getItemById(Long id);

    List<Item> getAllItemsUser(Long userId);

    List<Item> search(String text);
}