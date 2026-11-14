package net.space.developer.meetingscheduler.adapter.out.persistence;

import net.space.developer.meetingscheduler.domain.model.TimeSlot;
import org.springframework.data.jpa.domain.Specification;

/**
 * Composable filters for the booking search; a {@code null} argument adds no restriction.
 */
final class BookingSpecifications {

    private BookingSpecifications() {
    }

    static Specification<BookingJpaEntity> matching(Long roomId, TimeSlot window) {
        return Specification.<BookingJpaEntity>unrestricted()
                .and(inRoom(roomId))
                .and(overlapping(window));
    }

    private static Specification<BookingJpaEntity> inRoom(Long roomId) {
        return roomId == null
                ? Specification.unrestricted()
                : (root, query, cb) -> cb.equal(root.get("roomId"), roomId);
    }

    private static Specification<BookingJpaEntity> overlapping(TimeSlot window) {
        return window == null
                ? Specification.unrestricted()
                : (root, query, cb) -> cb.and(
                        cb.lessThan(root.get("startTime"), window.end()),
                        cb.greaterThan(root.get("endTime"), window.start()));
    }
}
