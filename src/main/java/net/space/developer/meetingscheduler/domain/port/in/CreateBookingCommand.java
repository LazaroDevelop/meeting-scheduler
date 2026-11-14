package net.space.developer.meetingscheduler.domain.port.in;

import java.time.Instant;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief CreateBookingCommand is a command object for creating a new booking.
 */
public record CreateBookingCommand(Long roomId, String personInCharge, Instant startTime, Instant endTime) {
}
