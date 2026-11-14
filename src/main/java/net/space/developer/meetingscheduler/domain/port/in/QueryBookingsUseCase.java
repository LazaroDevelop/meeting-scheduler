package net.space.developer.meetingscheduler.domain.port.in;

import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.PageResult;

public interface QueryBookingsUseCase {

    Booking findById(Long id);

    PageResult<Booking> search(BookingSearchQuery query);
}
