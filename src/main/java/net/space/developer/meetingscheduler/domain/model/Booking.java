package net.space.developer.meetingscheduler.domain.model;

import net.space.developer.meetingscheduler.domain.exception.BookingAlreadyCancelledException;

public record Booking(Long id, Long roomId, String personInCharge, TimeSlot slot, BookingStatus status) {

    public static Booking create(Long roomId, String personInCharge, TimeSlot slot) {
        return new Booking(null, roomId, personInCharge, slot, BookingStatus.ACTIVE);
    }

    public boolean isActive() {
        return status == BookingStatus.ACTIVE;
    }

    public Booking cancel() {
        if (!isActive()) {
            throw new BookingAlreadyCancelledException(id);
        }
        return new Booking(id, roomId, personInCharge, slot, BookingStatus.CANCELLED);
    }
}
