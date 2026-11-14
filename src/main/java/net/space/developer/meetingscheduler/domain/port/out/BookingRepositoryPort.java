package net.space.developer.meetingscheduler.domain.port.out;

import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;

import java.util.Optional;

public interface BookingRepositoryPort {

    Booking save(Booking booking);

    Optional<Booking> findById(Long id);

    /**
     * Whether an active booking of the room intersects the given slot.
     */
    boolean existsOverlapping(Long roomId, TimeSlot slot);
}
