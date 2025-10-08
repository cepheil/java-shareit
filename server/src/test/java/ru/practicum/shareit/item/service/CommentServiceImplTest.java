package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Item item;

    @BeforeEach
    void init() {
        user = new User(1L, "Ivan", "ivan@mail.ru");
        item = new Item(10L, "Вещь", "Описание", user, true, null);
    }

    @Test
    @DisplayName("createComment — бросает ValidationException при userId/itemId = null")
    void createComment_shouldThrow_whenIdsNull() {
        assertThatThrownBy(() -> commentService.createComment(null, 1L, new CommentCreateDto("text")))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> commentService.createComment(1L, null, new CommentCreateDto("text")))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("createComment — бросает NotFoundException если пользователь не найден")
    void createComment_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> commentService.createComment(1L, 1L, new CommentCreateDto("text")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("createComment — бросает NotFoundException если вещь не найдена")
    void createComment_shouldThrow_whenItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(1L, 1L, new CommentCreateDto("text")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("createComment — бросает ValidationException если нет завершённого бронирования")
    void createComment_shouldThrow_whenNoBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                anyLong(), anyLong(), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThatThrownBy(() -> commentService.createComment(1L, 1L, new CommentCreateDto("text")))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Комментарий можно оставить только после завершения бронирования");
    }

    @Test
    @DisplayName("createComment — успешно создаёт комментарий при валидных данных")
    void createComment_shouldCreateSuccessfully() {
        Comment comment = new Comment(3L, "Хорошая вещь", LocalDateTime.now(), item, user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                anyLong(), anyLong(), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.createComment(1L, 10L, new CommentCreateDto("Хорошая вещь"));

        assertThat(result.getText()).isEqualTo("Хорошая вещь");
        assertThat(result.getAuthorName()).isEqualTo("Ivan");
        verify(commentRepository, times(1)).save(any(Comment.class));
    }


    @Test
    @DisplayName("getCommentsByItem — бросает ValidationException при itemId = null")
    void getCommentsByItem_shouldThrow_whenIdNull() {
        assertThatThrownBy(() -> commentService.getCommentsByItem(null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("getCommentsByItem — возвращает список комментариев при валидных данных")
    void getCommentsByItem_shouldReturnComments() {
        Comment comment = new Comment(1L, "Отлично", LocalDateTime.now(), item, user);
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));

        List<CommentDto> result = commentService.getCommentsByItem(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getText()).isEqualTo("Отлично");
    }
}