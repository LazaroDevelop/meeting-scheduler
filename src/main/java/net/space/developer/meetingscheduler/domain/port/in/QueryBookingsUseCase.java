package net.space.developer.meetingscheduler.domain.port.in;

import net.space.developer.meetingscheduler.domain.model.Booking;

public interface QueryBookingsUseCase {

    Booking findById(Long id);
}
