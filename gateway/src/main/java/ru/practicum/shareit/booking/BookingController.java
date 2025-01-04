package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                @Valid @RequestBody CreateBookingDto createBookingDto
                                                ) {
        return bookingClient.create(bookerId, createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        return bookingClient.update(ownerId, bookingId, approved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long id) {
        return bookingClient.findBookingById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsersBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                      @PositiveOrZero @RequestParam (name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam (name = "size", defaultValue = "10") Integer size) {
        BookingState stateParam = BookingState.fromString(state);
        return bookingClient.getBookings(bookerId, stateParam, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState stateParam = BookingState.fromString(state);
        return bookingClient.getAllOwnerBookings(ownerId, stateParam, from, size);
    }
}