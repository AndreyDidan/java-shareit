package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInItemDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemRequestInItemDto itemRequestInItemDto;
    private ItemsRequestDto itemsRequestDto;
    private final String header = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("test");
        itemDto.setDescription("testDescription");
        itemDto.setAvailable(true);

        itemsRequestDto = new ItemsRequestDto();
        itemsRequestDto.setId(1L);
        itemsRequestDto.setName("test");
        itemsRequestDto.setDescription("testDescription");
        itemsRequestDto.setAvailable(true);

        itemRequestInItemDto = new ItemRequestInItemDto();
        itemRequestInItemDto.setId(1L);
        itemRequestInItemDto.setDescription("testDescription");
        itemRequestInItemDto.setCreated(LocalDateTime.now());
        itemRequestInItemDto.setRequestor(null);
        itemRequestInItemDto.setItems(List.of(itemsRequestDto));
    }

    @Test
    void create() throws Exception {
        when(itemRequestService.create(anyLong(), any())).thenReturn(itemRequestInItemDto);
        mvc.perform(post("/requests").content(mapper.writeValueAsString(itemRequestInItemDto))
                .header(header, 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestInItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestInItemDto.getDescription())));
    }

    @Test
    void findByAuthor() throws Exception {
        when(itemRequestService.getUserRequests(anyLong())).thenReturn(List.of(itemRequestInItemDto));
        mvc.perform(get("/requests").header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestInItemDto.getId()), Long.class));
    }

    @Test
    void findAll() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestInItemDto));

        mvc.perform(get("/requests/all").header(header, 1))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestInItemDto.getId()), Long.class));
    }

    @Test
    void findById() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestInItemDto);

        mvc.perform(get("/requests/{id}",1).header(header,1))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestInItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is("testDescription")))
                .andExpect(jsonPath("$.items[0].id", is(1)))
                .andExpect(jsonPath("$.items[0].name", is("test")));
    }

    @Test
    void getNotFoundRequest() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Запрос не найден"));

        mvc.perform(get("/requests/{id}", 1).header(header, 1))
                .andExpect(status().isNotFound());
    }
}