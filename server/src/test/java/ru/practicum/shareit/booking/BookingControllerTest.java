package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemsRequestDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import org.springframework.http.MediaType;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;
    private final String header = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        User user = new User(1L,"Andrey", "andrey@mail.ru");
        ItemsRequestDto itemsRequestDto = new ItemsRequestDto(
                1L, "testName", "testDescription", true, List.of(), null);
        bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(25), itemsRequestDto,
                UserMapper.mapToUserDto(user), Status.WAITING);
    }

    @Test
    void createBooking() throws Exception {
        CreateBookingDto request = new CreateBookingDto();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusDays(1));

        when(bookingService.create(anyLong(), any())).thenReturn(bookingDto);
        mvc.perform(post("/bookings").content(mapper.writeValueAsString(request))
                        .header(header, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void createBookingBadRequest() throws Exception {
        CreateBookingDto request = new CreateBookingDto();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().minusHours(25));

        when(bookingService.create(anyLong(), any())).thenReturn(bookingDto);
        mvc.perform(post("/bookings").content(mapper.writeValueAsString(request))
                        .header(header, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);
        mvc.perform(patch("/bookings/{id}", 1).param("approved", "true").header(header, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong())).thenReturn(bookingDto);
        mvc.perform(get("/bookings/{id}", 1).header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getAllUsersBookings() throws Exception {
        when(bookingService.getAllUsersBookings(anyLong(), any())).thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings", 1).header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getAllOwnerBookings() throws Exception {
        when(bookingService.getAllOwnerBookings(anyLong(), any())).thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings/owner", 1).header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBookingNotFoundException() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Бронирование id = \" + bookingId + \" не найдено"));
        mvc.perform(get("/bookings/{id}", 1).header(header, 1))
                .andExpect(status().isNotFound());
    }
}