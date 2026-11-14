package net.space.developer.meetingscheduler.domain.port.in;

import java.time.LocalDate;

/**
 * Booking search; {@code roomId} and {@code date} (UTC day) are optional filters.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public record BookingSearchQuery(Long roomId, LocalDate date, int page, int size) {
}
