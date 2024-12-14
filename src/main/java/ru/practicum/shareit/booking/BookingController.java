package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                    @Valid @RequestBody CreateBookingDto createBookingDto) {
        if (createBookingDto.getEnd().isBefore(createBookingDto.getStart())) {
            throw new BadRequestException("Дата окончания бронирования не может быть раньше даты начала.");
        }
        if (bookingService.isBookingOverlapping(createBookingDto)) {
            throw new BadRequestException("Новое бронирование пересекается по времени с уже существующими.");
        }

        return bookingService.create(bookerId, createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        return bookingService.update(ownerId, bookingId, approved);
    }

    @GetMapping("/{id}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long id) {
        return bookingService.findBookingById(userId, id);
    }

    @GetMapping
    public List<BookingDto> getAllUsersBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllUsersBookings(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllOwnerBookings(ownerId, state);
    }
}