package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long bookerId, CreateBookingDto createBookingDto);

    BookingDto update(Long userId, Long bookingId, Boolean approved);

    BookingDto findBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllUsersBookings(Long userId, String state);

    List<BookingDto> getAllOwnerBookings(Long userId, String state);
}