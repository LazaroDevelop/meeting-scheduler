package net.space.developer.meetingscheduler.domain.exception;

import net.space.developer.meetingscheduler.domain.model.TimeSlot;

public class BookingConflictException extends ConflictException {

    public BookingConflictException(Long roomId, TimeSlot slot) {
        super("Room %s is already booked between %s and %s".formatted(roomId, slot.start(), slot.end()));
    }
}
