package net.space.developer.meetingscheduler.domain.port.in;

import java.time.Instant;

/**
 * CreateBookingCommand is a command object for creating a new booking.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public record CreateBookingCommand(Long roomId, String personInCharge, Instant startTime, Instant endTime) {
}
