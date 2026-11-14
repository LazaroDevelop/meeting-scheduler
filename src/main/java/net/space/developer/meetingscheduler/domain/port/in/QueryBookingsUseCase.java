package net.space.developer.meetingscheduler.domain.port.in;

import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.PageResult;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief QueryBookingsUseCase is a use case interface for querying booking information.
 */
public interface QueryBookingsUseCase {

    /**
     * Finds a booking by its unique identifier.
     * 
     * @param id the unique identifier of the booking
     * @return the booking with the specified id
     */
    Booking findById(Long id);

    /**
     * Searches for bookings based on the provided query.
     * 
     * @param query the search query
     * @return a page of matching bookings
     */
    PageResult<Booking> search(BookingSearchQuery query);
}
