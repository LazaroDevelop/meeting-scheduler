package net.space.developer.meetingscheduler.domain.port.in;

import net.space.developer.meetingscheduler.domain.model.Booking;

/**
 * CreateBookingUseCase is a use case interface for creating booking information.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public interface CreateBookingUseCase {

    /**
     * Creates a new booking based on the provided command.
     * 
     * @param command the command containing booking creation details
     * @return the created booking
     */
    Booking create(CreateBookingCommand command);
}
