package ru.practicum.shareit.booking;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@NoArgsConstructor
public class BookingMapper {
    public static Booking mapToBooking(CreateBookingDto createBookingDto, User user, Item item) {
        return Booking.builder()
                .start(createBookingDto.getStart())
                .end(createBookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.mapToItemDto(booking.getItem()))
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDto> mapToListBookingDto(List<Booking> bookingList) {
        return bookingList.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }
}