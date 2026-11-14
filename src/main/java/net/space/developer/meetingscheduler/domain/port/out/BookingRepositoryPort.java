package net.space.developer.meetingscheduler.domain.port.out;

import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.PageResult;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;

import java.util.Optional;

/**
 * BookingRepositoryPort is the interface that defines the contract for booking repository operations in the domain layer.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public interface BookingRepositoryPort {

    /**
     * Saves a booking to the repository.
     * 
     * @param booking - The booking object to be saved.
     * @return The saved booking object, potentially with updated fields (e.g., ID).
     */
    Booking save(Booking booking);

    /**
     * Finds a booking by its unique identifier.
     * 
     * @param id - The unique identifier of the booking.
     * @return An Optional containing the Booking if found, or empty if not found.
     */
    Optional<Booking> findById(Long id);

    /**
     * Whether an active booking of the room intersects the given slot.
     */
    boolean existsOverlapping(Long roomId, TimeSlot slot);

    /**
     * Bookings ordered by start time; a {@code null} room or window means "no filter".
     */
    PageResult<Booking> search(Long roomId, TimeSlot window, int page, int size);
}
