package net.space.developer.meetingscheduler.adapter.in.web.dto;

import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.BookingStatus;

import java.time.Instant;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * BookingResponse is the response DTO for a booking.
 * @param id - The unique identifier of the booking.
 * @param roomId - The unique identifier of the room associated with the booking.
 * @param personInCharge - The name of the person responsible for the booking.
 * @param startTime - The start time of the booking.
 * @param endTime - The end time of the booking.
 * @param status - The status of the booking.
 */
public record BookingResponse(
        Long id, Long roomId, String personInCharge, Instant startTime, Instant endTime, BookingStatus status) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(booking.id(), booking.roomId(), booking.personInCharge(),
                booking.slot().start(), booking.slot().end(), booking.status());
    }
}
