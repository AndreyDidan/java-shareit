package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemsRequestDto createItem(Long userId, CreateItemDto createItemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден!"));
        if (createItemDto.getName() == null || createItemDto.getName().isBlank()) {
            throw new ValidationException("Поле \"name\" обязательно для заполнения!");
        }
        Item item = new Item();
        if (createItemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(createItemDto.getRequestId()).orElse(null);
            if (itemRequest != null) {
                item = ItemMapper.mapToItem(createItemDto, itemRequest);
            } else {
                item = ItemMapper.mapToItem(createItemDto);
            }
        } else {
            item = ItemMapper.mapToItem(createItemDto);
        }
        item.setOwner(owner);
        itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemsRequestDto updateItem(Long userId, Long itemId, ItemDto itemDto) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена!"));
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден!"));
        if (itemDto.getOwner() != null && !itemDto.getOwner().getId().equals(userId)) {
            throw new ValidationException("Вещь с id " + itemId + " не пренадлежит пользователю с id " + userId);
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item = ItemMapper.mapToUpdatedItem(item, itemDto, itemId, owner);
        itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id = "
                + itemId + " не найдена!"));
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = "
                + userId + " не найден!"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Вещь с id " + itemId + " не пренадлежит пользователю с id " + userId);
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemCommentDto getItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + id + " не найдена"));
        List<CommentDto> comments = commentRepository.findAllByItemId(id).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
        BookingDto lastBooking = null;

        Optional<Booking> recentBooking = bookingRepository.findByItemId(id).stream()
                .filter(booking -> booking.getStatus() == Status.APPROVED)
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd));

        if (recentBooking.isPresent()) {
            if (recentBooking.get().getBooker().getId().equals(userId)) {
                lastBooking = null;
            } else {
                lastBooking = BookingMapper.mapToBookingDto(recentBooking.get());
            }
        }

        BookingDto nextBooking = bookingRepository.findByItemId(id).stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()) &&
                        booking.getStatus() == Status.APPROVED)
                .min(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::mapToBookingDto)
                .orElse(null);
        return ItemMapper.mapToItemAllDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemsRequestDto> getAllItemsUser(Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден!"));
        return itemRepository.findAllByOwnerId(owner.getId()).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemsRequestDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CreateCommentDto createCommentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        boolean hasCompletedBooking = !bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, Status.APPROVED, LocalDateTime.now()).isEmpty();
        if (!hasCompletedBooking) {
            throw new BadRequestException("Пользователь не может комментировать эту вещь, так как он её не " +
                    "бронировал или срок бронирования не завершён");
        }

        Comment comment = CommentMapper.mapToComment(createCommentDto, user, item);
        commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment);
    }
}