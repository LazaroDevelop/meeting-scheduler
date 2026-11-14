package net.space.developer.meetingscheduler.adapter.in.web.dto;

import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.BookingStatus;

import java.time.Instant;

public record BookingResponse(
        Long id, Long roomId, String personInCharge, Instant startTime, Instant endTime, BookingStatus status) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(booking.id(), booking.roomId(), booking.personInCharge(),
                booking.slot().start(), booking.slot().end(), booking.status());
    }
}
