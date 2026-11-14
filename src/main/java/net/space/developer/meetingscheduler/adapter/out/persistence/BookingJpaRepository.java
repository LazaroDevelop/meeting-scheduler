package net.space.developer.meetingscheduler.adapter.out.persistence;

import net.space.developer.meetingscheduler.domain.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief JPA repository for managing booking entities.
 */
interface BookingJpaRepository extends JpaRepository<BookingJpaEntity, Long>, JpaSpecificationExecutor<BookingJpaEntity> {

    /**
     * Checks if there exists any booking for a given room that overlaps with the specified time range and has the specified status.
     * @param roomId - The unique identifier of the room.
     * @param status - The status of the booking to check for.
     * @param start - The start time of the time range to check for overlaps.
     * @param end - The end time of the time range to check for overlaps.
     * @return true if there exists an overlapping booking, false otherwise.
     */
    @Query("""
            select count(b) > 0 from BookingJpaEntity b
            where b.roomId = :roomId
              and b.status = :status
              and b.startTime < :end
              and b.endTime > :start
            """)
    boolean existsOverlapping(Long roomId, BookingStatus status, Instant start, Instant end);
}
