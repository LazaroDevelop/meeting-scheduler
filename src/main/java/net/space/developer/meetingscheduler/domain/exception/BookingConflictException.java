package net.space.developer.meetingscheduler.domain.exception;

import net.space.developer.meetingscheduler.domain.model.TimeSlot;

/**
 * Exception thrown when attempting to create a booking that conflicts with an existing booking
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public class BookingConflictException extends ConflictException {

    /**
     * Constructs a new BookingConflictException with a message indicating the room ID and the conflicting time slot.
     * 
     * @param roomId the ID of the room that is already booked
     * @param slot the time slot that conflicts with an existing booking
     */
    public BookingConflictException(Long roomId, TimeSlot slot) {
        super("Room %s is already booked between %s and %s".formatted(roomId, slot.start(), slot.end()));
    }
}
