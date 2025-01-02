package ru.practicum.shareit.item.comment.repository;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByItemId() {
        User user = new User(null, "test@gmail.com", "name");
        Item item = new Item(null, "testItem", "testDescr", true, user, null);
        Comment comment = new Comment(null, "text", item, user, LocalDateTime.now());
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(comment);
        entityManager.flush();

        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        assertThat(comments.size(), equalTo(1));
        assertThat(comments.getFirst(), equalTo(comment));
    }
}