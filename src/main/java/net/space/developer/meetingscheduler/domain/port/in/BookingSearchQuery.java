package net.space.developer.meetingscheduler.domain.port.in;

import java.time.LocalDate;

/**
 * Booking search; {@code roomId} and {@code date} (UTC day) are optional filters.
 */
public record BookingSearchQuery(Long roomId, LocalDate date, int page, int size) {
}
