package net.space.developer.meetingscheduler.domain.exception;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief Exception thrown when a booking is not found in the system
 */
public class BookingNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs a new BookingNotFoundException with a message indicating the booking ID that was not found.
     * 
     * @param id the ID of the booking that was not found
     */
    public BookingNotFoundException(Long id) {
        super("Booking", id);
    }
}
