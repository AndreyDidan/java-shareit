package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByBookerIdAndStatus() {
        User user = new User(null, "Andrey", "andrey@mail.ru");
        Item item = new Item(null, "testItem", "test item descri", true, user, null);
        Booking booking = new Booking(null, LocalDateTime.now(), LocalDateTime.now().plusHours(25),
                item, user, Status.WAITING);
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking);
        entityManager.flush();

        List<Booking> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(user.getId(),
                Status.WAITING);
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.getFirst(), equalTo(booking));
    }
}