package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByOwnerId() {
    }

    @Test
    void search() {
        User user = new User(null, "test@gmail.com", "testName");
        Item item = new Item(null, "testItem", "testDescr", true, user, null);
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.flush();

        List<Item> items = itemRepository.search("te");
        assertThat(items.size(), equalTo(1));
        assertThat(items.getFirst(), equalTo(item));
    }

    @Test
    void findByRequestIn() {
        User user = new User(null, "mail@du.tu", "name");
        ItemRequest request = new ItemRequest(null, "description", user, LocalDateTime.now());
        Item item = new Item(null, "item", "item descr", true, user, request);
        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.persist(item);
        entityManager.flush();

        List<Item> items = itemRepository.findByRequestIn(List.of(request), Sort.by(Sort.Direction.ASC, "id"));
        assertThat(items.size(), equalTo(1));
        assertThat(items.getFirst(), equalTo(item));
    }

    @Test
    void findByRequestId() {
        User user = new User(null, "test@mail.ru", "testName");
        ItemRequest request = new ItemRequest(null, "testDescription", user, LocalDateTime.now());
        Item item = new Item(null, "testItem", "testDescription", true, user, request);
        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.persist(item);
        entityManager.flush();

        List<Item> items = itemRepository.findByRequestId(request.getId(), Sort.by(Sort.Direction.ASC, "id"));
        assertThat(items.size(), equalTo(1));
        assertThat(items.getFirst(), equalTo(item));
    }
}