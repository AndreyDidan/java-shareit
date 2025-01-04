package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class BookingServiceImplTest {

    @Autowired
    BookingService bookingService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private CreateBookingDto createBookingDto;
    private UserDto userDto;
    private ItemsRequestDto itemsRequestDto;

    @BeforeEach
    void setUp() {
        userDto = userService.createUser(new CreateUserDto("Andrey", "andrey@mail.ru"));
        itemsRequestDto = itemService.createItem(userDto.getId(), new CreateItemDto("testNameItem",
                "testDescriptionItem", true, null));
        createBookingDto = new CreateBookingDto(itemsRequestDto.getId(), LocalDateTime.now(),
                LocalDateTime.now().plusHours(25));
    }

    @Test
    void create() {
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();

        TypedQuery<Booking> query = entityManager.createQuery("select booking from Booking booking where booking.id = :id", Booking.class);
        Booking booking = query.setParameter("id", id).getSingleResult();
        assertThat(booking.getId(), equalTo(id));
        assertThat(booking.getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(booking.getBooker().getId(), equalTo(userDto.getId()));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void createBookingStartException() {
        createBookingDto.setEnd(LocalDateTime.now().minusHours(25));
        assertThrows(BadRequestException.class, () -> bookingService.create(userDto.getId(), createBookingDto));
    }

    @Test
    void createBookingItemException() {
        itemService.updateItem(userDto.getId(), itemsRequestDto.getId(), ItemDto.builder().available(false).build());
        assertThrows(BadRequestException.class, () -> bookingService.create(userDto.getId(), createBookingDto));
    }

    @Test
    void update() {
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();
        bookingService.update(userDto.getId(), id, true);

        BookingDto bookingDtoS = bookingService.findBookingById(userDto.getId(), id);
        assertThat(bookingDtoS.getId(), equalTo(id));
        assertThat(bookingDtoS.getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(bookingDtoS.getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void updateBookingException() {
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();
        assertThrows(BadRequestException.class, () -> bookingService.update(255L, id, true));
    }

    @Test
    void findBookingById() {
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();

        BookingDto bookingDto = bookingService.findBookingById(userDto.getId(), id);
        assertThat(bookingDto.getId(), equalTo(id));
        assertThat(bookingDto.getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(bookingDto.getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDto.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void getBookingNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(255L, 255L));
    }

    @Test
    void getAllUsersBookings() {
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();

        List<BookingDto> bookingDtoS = bookingService.getAllUsersBookings(userDto.getId(), "All");
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void getAllUsersBookingsStateWaiting() {
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();

        List<BookingDto> bookingDtoS = bookingService.getAllUsersBookings(userDto.getId(), "WAITING");
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void getBookingsStateRejected() {
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();
        bookingService.update(userDto.getId(), id, false);

        List<BookingDto> bookingDtoS = bookingService.getAllUsersBookings(userDto.getId(), "REJECTED");
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void getBookingsStateFuture() {
        createBookingDto.setStart(LocalDateTime.now().plusMinutes(1));
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();

        List<BookingDto> bookingDtoS = bookingService.getAllUsersBookings(userDto.getId(), "FUTURE");
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void getBookingsByStatePast() {
        createBookingDto.setStart(LocalDateTime.now().minusMinutes(10));
        createBookingDto.setEnd(LocalDateTime.now().minusMinutes(5));
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();

        List<BookingDto> bookingDtoS = bookingService.getAllUsersBookings(userDto.getId(), "PAST");
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(Status.WAITING));
    }


    @Test
    void getAllOwnerBookings() {
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();

        List<BookingDto> bookingDtoS = bookingService.getAllOwnerBookings(userDto.getId(), "All");
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void getBookingsOwnerCurrent() {
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();
        bookingService.update(userDto.getId(), id, true);

        List<BookingDto> bookingDtoS = bookingService.getAllOwnerBookings(userDto.getId(), "CURRENT");
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void getBookingsOwnerRejected() {
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();
        bookingService.update(userDto.getId(), id, false);

        List<BookingDto> bookingDtoS = bookingService.getAllOwnerBookings(userDto.getId(),"REJECTED");
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void getBookingsOwnerFuture() {
        createBookingDto.setStart(LocalDateTime.now().plusMinutes(1));
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();

        List<BookingDto> bookingDtoS = bookingService.getAllOwnerBookings(userDto.getId(), "FUTURE");
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void getBookingsByOwnerPast() {
        createBookingDto.setStart(LocalDateTime.now().minusMinutes(10));
        createBookingDto.setEnd(LocalDateTime.now().minusMinutes(5));
        Long id = bookingService.create(userDto.getId(), createBookingDto).getId();

        List<BookingDto> bookingDtoS = bookingService.getAllOwnerBookings(userDto.getId(), "PAST");
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(Status.WAITING));
    }
}