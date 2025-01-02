package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemsRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestInItemDto create(Long userId, CreateItemRequestDto createItemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден!"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(createItemRequestDto, user);

        return ItemRequestMapper.toItemRequestInItemDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestInItemDto> getUserRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден!"));
        List<ItemRequest> requestsAuthor = itemRequestRepository
                .findAllByRequestorId(userId, Sort.by(DESC, "created"));
        Map<ItemRequest, List<Item>> mapRequest = itemRepository
                .findByRequestIn(requestsAuthor, Sort.by(ASC, "id"))
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));
        return requestsAuthor.stream()
                .map(itemRequest -> setItemRequestItems(itemRequest, mapRequest.get(itemRequest)))
                .collect(toList());
    }

    @Override
    public List<ItemRequestInItemDto> getAllRequests(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден!"));
        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by(DESC, "created"));
        List<ItemRequest> responseDtoList = itemRequestRepository.findAllByRequestorIdNot(userId, pageRequest);
        Map<ItemRequest, List<Item>> itemsMap = itemRepository.findByRequestIn(responseDtoList, Sort.by(ASC, "id"))
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));
        return responseDtoList.stream()
                .map(itemRequest -> setItemRequestItems(itemRequest, itemsMap.get(itemRequest)))
                .collect(toList());
    }

    @Override
    public ItemRequestInItemDto getRequestById(Long requestId, Long userId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElse(null);
        if (itemRequest == null) {
            return ItemRequestInItemDto.builder()
                    .id(requestId)
                    .description("Запрос не найден")
                    .build();
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден!"));
        List<Item> itemList = itemRepository.findByRequestId(requestId, Sort.by(ASC, "id"));
        return setItemRequestItems(itemRequest, itemList);
    }

    private ItemRequestInItemDto setItemRequestItems(ItemRequest itemRequest, List<Item> items) {
        List<ItemsRequestDto> itemResponseDtoList = new ArrayList<>();
        if (items != null && !items.isEmpty()) {
            for (Item item : items) {
                itemResponseDtoList.add(ItemMapper.mapToItemDto(item));
            }
        }
        ItemRequestInItemDto responseDto = ItemRequestMapper.toItemRequestInItemDto(itemRequest);
        responseDto.setItems(itemResponseDtoList);
        return responseDto;
    }
}
