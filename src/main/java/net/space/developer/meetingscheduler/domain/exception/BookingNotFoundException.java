package net.space.developer.meetingscheduler.domain.exception;

public class BookingNotFoundException extends ResourceNotFoundException {

    public BookingNotFoundException(Long id) {
        super("Booking", id);
    }
}
