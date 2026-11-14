package net.space.developer.meetingscheduler.adapter.out.persistence;

import net.space.developer.meetingscheduler.domain.model.TimeSlot;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief Composable filters for the booking search; a {@code null} argument adds no restriction.
 */
final class BookingSpecifications {

    private BookingSpecifications() {
    }

    /**
     * Creates a specification that filters bookings by room ID and time slot window.
     * 
     * @param roomId - The unique identifier of the room to filter bookings by. If null, no filtering by room is applied.
     * @param window - The time slot window to filter bookings by. If null, no filtering by time slot is applied.
     * @return A Specification object that can be used to query bookings based on the provided room ID and time slot window.
     */
    static Specification<BookingJpaEntity> matching(Long roomId, TimeSlot window) {
        return Specification.<BookingJpaEntity>unrestricted()
                .and(inRoom(roomId))
                .and(overlapping(window));
    }

    /**
     * Creates a specification that filters bookings by room ID.
     * 
     * @param roomId - The unique identifier of the room to filter bookings by. If null, no filtering by room is applied.
     * @return A Specification object that can be used to query bookings based on the provided room ID.
     */
    private static Specification<BookingJpaEntity> inRoom(Long roomId) {
        return roomId == null
                ? Specification.unrestricted()
                : (root, query, cb) -> cb.equal(root.get("roomId"), roomId);
    }

    /**
     * Creates a specification that filters bookings by overlapping time slots.
     * 
     * @param window - The time slot window to filter bookings by. If null, no filtering by time slot is applied.
     * @return A Specification object that can be used to query bookings based on the provided time slot window.
     */
    private static Specification<BookingJpaEntity> overlapping(TimeSlot window) {
        return window == null
                ? Specification.unrestricted()
                : (root, query, cb) -> cb.and(
                        cb.lessThan(root.get("startTime"), window.end()),
                        cb.greaterThan(root.get("endTime"), window.start()));
    }
}
