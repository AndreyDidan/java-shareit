package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemCommentDto itemCommentDto;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private CreateItemDto createItemDto;
    private ItemsRequestDto itemsRequestDto;
    private final String header = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        User user = new User(1L, "Andrey", "andrey@mail.ru");
        itemDto = new ItemDto(1L, "test", "test", true, user, null);
        itemCommentDto = new ItemCommentDto();
        itemCommentDto.setId(1L);
        itemCommentDto.setName("test");
        itemCommentDto.setDescription("test");
        itemCommentDto.setAvailable(true);
        itemCommentDto.setOwner(user);

        itemsRequestDto = new ItemsRequestDto(1L, "test", "test", true,
                List.of(), null);
        createItemDto = new CreateItemDto("test", "test", true, null);

        commentDto = new CommentDto(1L, "test", itemDto.getId(), user.getName(), LocalDateTime.now());
    }

    @Test
    void getAllItemsUser() throws Exception {
        when(itemService.getAllItemsUser(anyLong())).thenReturn(List.of(itemsRequestDto));
        mvc.perform(get("/items", 1).header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(itemCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemCommentDto.getName())));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemCommentDto);
        mvc.perform(get("/items/{id}", 1).header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemCommentDto.getName())))
                .andExpect(jsonPath("$.description", is(itemCommentDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemCommentDto.getAvailable())));
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(anyLong(), any())).thenReturn(itemsRequestDto);
        mvc.perform(post("/items").header(header, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(itemsRequestDto);
        mvc.perform(patch("/items/{id}", 1).header(header, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);
        mvc.perform(post("/items/{id}/comment", 1).header(header, 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void search() throws Exception {
        when(itemService.search(anyString())).thenReturn(List.of(itemsRequestDto));

        mvc.perform(get("/items/search").header(header, 1)
                        .param("text", "desc")
                        .header(header, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(itemsRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemsRequestDto.getName())));
    }

    @Test
    void getItemNotFoundException() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Item not found"));
        mvc.perform(get("/items/{id}", 255).header(header, 1))
                .andExpect(status().isNotFound());
    }
}