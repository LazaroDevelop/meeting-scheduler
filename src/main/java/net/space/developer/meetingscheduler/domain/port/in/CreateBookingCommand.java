package net.space.developer.meetingscheduler.domain.port.in;

import java.time.Instant;

public record CreateBookingCommand(Long roomId, String personInCharge, Instant startTime, Instant endTime) {
}
