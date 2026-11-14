package net.space.developer.meetingscheduler.domain.port.in;

/**
 * CancelBookingUseCase is a use case interface for canceling booking information.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public interface CancelBookingUseCase {
    /**
     * Cancels a booking based on the provided booking ID.
     * 
     * @param bookingId the unique identifier of the booking to be canceled
     */
    void cancel(Long bookingId);
}
