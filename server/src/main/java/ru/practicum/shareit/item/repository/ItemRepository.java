package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long id);

    @Query("SELECT i FROM Item AS i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND i.available = TRUE")
    List<Item> search(String text);

    List<Item> findByRequestIn(List<ItemRequest> request, Sort sort);

    List<Item> findByRequestId(Long itemRequestId, Sort sort);
}