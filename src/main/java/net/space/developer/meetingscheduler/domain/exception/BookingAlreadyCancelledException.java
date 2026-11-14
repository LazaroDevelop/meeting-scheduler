package net.space.developer.meetingscheduler.domain.exception;

public class BookingAlreadyCancelledException extends ConflictException {

    public BookingAlreadyCancelledException(Long id) {
        super("Booking %s is already cancelled".formatted(id));
    }
}
