package net.space.developer.meetingscheduler.domain.model;

import net.space.developer.meetingscheduler.domain.exception.InvalidTimeRangeException;

import java.time.Instant;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief Half-open time interval {@code [start, end)}: back-to-back slots do not overlap.
 */
public record TimeSlot(Instant start, Instant end) {
    
    /**
     * Constructs a new TimeSlot instance with the specified start and end times.
     *
     * @param start The start time of the slot.
     * @param end The end time of the slot.
     */
    public TimeSlot {
        if (start == null || end == null) {
            throw new InvalidTimeRangeException("Start and end time are required");
        }
        if (!end.isAfter(start)) {
            throw new InvalidTimeRangeException("End time must be after start time");
        }
    }

    /**
     * Checks if this time slot overlaps with another time slot.
     *
     * @param other The other time slot to check for overlap.
     * @return true if the time slots overlap, false otherwise.
     */
    public boolean overlaps(TimeSlot other) {
        return start.isBefore(other.end) && end.isAfter(other.start);
    }
}
