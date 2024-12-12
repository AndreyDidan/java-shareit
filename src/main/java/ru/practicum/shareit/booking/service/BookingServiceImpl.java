package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.booking.BookingMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto create(Long bookerId, CreateBookingDto createBookingDto) {
        User user = findUserById(bookerId);
        Item item = itemRepository.findById(createBookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет с id = "
                        + createBookingDto.getItemId() + " не найден."));
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new BadRequestException("В настоящее время предмет не доступен для бронирования");
        }

        if (createBookingDto.getEnd().isBefore(createBookingDto.getStart())) {
            throw new BadRequestException("Дата окончания бронирования не может быть раньше даты начала");
        }
        Booking booking = BookingMapper.mapToBooking(createBookingDto, user, item);
        bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto update(Long userId, Long bookingId, Boolean approved) {

        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, userId)
                .orElseThrow(() -> new BadRequestException("Бронирование id = " + bookingId + " не найдено"));
        if (approved != null) {
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        }

        bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto findBookingById(Long userId, Long bookingId) {
        User user = findUserById(userId);
        Booking booking = findBookingById(bookingId);
        if (user.getId().equals(booking.getBooker().getId()) || user.getId().equals(booking.getItem().getOwner().getId())) {
            return BookingMapper.mapToBookingDto(booking);
        } else {
            throw new BadRequestException("У пользователя нет прав для просмотра бронирования");
        }
    }

    @Override
    @Transactional
    public List<BookingDto> getAllUsersBookings(Long userId, String state) {
        User booker = findUserById(userId);
        BookingState bookingState = validateBookingState(state);

        return switch (bookingState) {
            case ALL -> BookingMapper.mapToListBookingDto(
                    bookingRepository.findByBookerIdOrderByStartDesc(userId));
            case PAST -> BookingMapper.mapToListBookingDto(
                    bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(), LocalDateTime.now()));
            case CURRENT -> BookingMapper.mapToListBookingDto(
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.APPROVED));
            case FUTURE -> BookingMapper.mapToListBookingDto(
                    bookingRepository.findByBookerIdAndStartAfter(userId, LocalDateTime.now()));
            case WAITING -> BookingMapper.mapToListBookingDto(
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING));
            case REJECTED -> BookingMapper.mapToListBookingDto(
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED));
        };
    }

    @Override
    public List<BookingDto> getAllOwnerBookings(Long userId, String state) {
        User owner = findUserById(userId);
        BookingState bookingState = validateBookingState(state);

        return switch (bookingState) {
            case ALL -> BookingMapper.mapToListBookingDto(
                    bookingRepository.findByItemOwnerIdOrderByStartDesc(owner.getId()));
            case PAST -> BookingMapper.mapToListBookingDto(
                    bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()));
            case CURRENT -> BookingMapper.mapToListBookingDto(
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.APPROVED));
            case FUTURE -> BookingMapper.mapToListBookingDto(
                    bookingRepository.findByItemOwnerIdAndStartAfter(userId, LocalDateTime.now()));
            case WAITING -> BookingMapper.mapToListBookingDto(
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING));
            case REJECTED -> BookingMapper.mapToListBookingDto(
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED));
        };
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id =" + userId));
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id =" + bookingId));
    }

    private BookingState validateBookingState(String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Неподдерживаемый формат state: " + state);
        }
        return bookingState;
    }
}