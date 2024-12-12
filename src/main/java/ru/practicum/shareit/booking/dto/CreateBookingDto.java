package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingDto {
    @NotNull
    @Positive
    private Long itemId;

    @NotNull
    @FutureOrPresent(message = "Дата начала бронирования должна быть в будущем или настоящем")
    private LocalDateTime start;

    @NotNull
    @Future(message = "Дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;
}