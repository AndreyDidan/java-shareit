package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime time);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime time);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndItemIdAndStatusAndEndBefore(Long userId, Long itemId,
                                                                  Status status, LocalDateTime date);

    List<Booking> findByItemId(Long itemId);

    Optional<Booking> findByIdAndItemOwnerId(Long id, Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.status = ?2 AND ((b.start < ?4 AND b.end > ?3))")
    List<Booking> findOverlappingBookings(Long itemId, Status status, LocalDateTime start, LocalDateTime end);
}