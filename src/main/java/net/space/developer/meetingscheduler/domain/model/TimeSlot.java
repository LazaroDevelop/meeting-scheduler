package net.space.developer.meetingscheduler.domain.model;

import net.space.developer.meetingscheduler.domain.exception.InvalidTimeRangeException;

import java.time.Instant;

/**
 * Half-open time interval {@code [start, end)}: back-to-back slots do not overlap.
 */
public record TimeSlot(Instant start, Instant end) {

    public TimeSlot {
        if (start == null || end == null) {
            throw new InvalidTimeRangeException("Start and end time are required");
        }
        if (!end.isAfter(start)) {
            throw new InvalidTimeRangeException("End time must be after start time");
        }
    }

    public boolean overlaps(TimeSlot other) {
        return start.isBefore(other.end) && end.isAfter(other.start);
    }
}
