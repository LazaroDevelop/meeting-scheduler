package net.space.developer.meetingscheduler.domain.port.in;

public interface CancelBookingUseCase {

    void cancel(Long bookingId);
}
