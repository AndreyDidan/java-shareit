package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.BadRequestException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState fromString(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Неподдерживаемый формат state: " + state);
        }
    }
}