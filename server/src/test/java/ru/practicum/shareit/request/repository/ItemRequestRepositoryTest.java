package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.data.domain.Sort.Direction.ASC;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;
    private final Sort sortIdAsc = Sort.by(ASC, "id");
    private final PageRequest pageRequest = PageRequest.of(0, 20, sortIdAsc);

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByRequestorId() {
        User user = new User(null, "Andrey", "andrey@mail.ru");
        ItemRequest itemRequest = new ItemRequest(null, "testDescription", user, LocalDateTime.now());
        entityManager.persist(user);
        entityManager.persist(itemRequest);
        entityManager.flush();

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(user.getId(), sortIdAsc);

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.getFirst(), equalTo(itemRequest));
    }

    @Test
    void findAllByRequestorIdNot() {
        User user = new User(null, "Andrey", "andrey@mail.ru");
        ItemRequest itemRequest = new ItemRequest(null, "testDescription", user, LocalDateTime.now());
        entityManager.persist(user);
        entityManager.persist(itemRequest);
        entityManager.flush();

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNot(999L, pageRequest);

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.getFirst(), equalTo(itemRequest));
    }
}