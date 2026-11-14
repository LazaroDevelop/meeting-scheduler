package net.space.developer.meetingscheduler.domain.exception;

/**
 * Exception thrown when attempting to cancel a booking that is already cancelled
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public class BookingAlreadyCancelledException extends ConflictException {

    /**
     * Constructs a new BookingAlreadyCancelledException with a message indicating the booking ID that is already cancelled.
     * 
     * @param id the ID of the booking that is already cancelled
     */
    public BookingAlreadyCancelledException(Long id) {
        super("Booking %s is already cancelled".formatted(id));
    }
}
