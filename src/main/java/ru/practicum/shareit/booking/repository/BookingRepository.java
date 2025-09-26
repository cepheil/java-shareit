package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // --- для booker ---
    //получение всех бронирований по ID от новых к старым
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    //получение всех текущих бронирований по ID (start < now1 и end > now2)
    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now1, LocalDateTime now2);

    //получение всех завершенных бронирований по ID (end < now)
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(
            Long bookerId, LocalDateTime now);

    //получение всех будущих бронирований по ID (start > now)
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now);

    //получение всех бронирований по ID и статусу
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(
            Long bookerId, Status status);

    // --- для owner ---
    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(
            Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(
            Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(
            Long ownerId, Status status);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId,
            Long itemId,
            Status status,
            LocalDateTime end);


    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id = :itemId
              AND b.start < :currentTime
              AND b.status = :status
            ORDER BY b.start DESC
            """)
    List<Booking> findLastBooking(@Param("itemId") Long itemId,
                                  @Param("currentTime") LocalDateTime currentTime,
                                  @Param("status") Status status,
                                  Pageable pageable);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id = :itemId
              AND b.start > :currentTime
              AND b.status = :status
            ORDER BY b.start ASC
            """)
    List<Booking> findNextBooking(@Param("itemId") Long itemId,
                                  @Param("currentTime") LocalDateTime currentTime,
                                  @Param("status") Status status,
                                  Pageable pageable);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id IN :itemIds
              AND b.start < :currentTime
              AND b.status = 'APPROVED'
            ORDER BY b.start DESC
            """)
    List<Booking> findLastBookings(@Param("itemIds") List<Long> itemIds,
                                   @Param("currentTime") LocalDateTime currentTime);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id IN :itemIds
              AND b.start > :currentTime
              AND b.status = 'APPROVED'
            ORDER BY b.start ASC
            """)
    List<Booking> findNextBookings(@Param("itemIds") List<Long> itemIds,
                                   @Param("currentTime") LocalDateTime currentTime);

}

