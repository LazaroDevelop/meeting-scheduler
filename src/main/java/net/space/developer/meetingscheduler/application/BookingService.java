package net.space.developer.meetingscheduler.application;

import net.space.developer.meetingscheduler.domain.exception.BookingConflictException;
import net.space.developer.meetingscheduler.domain.exception.BookingNotFoundException;
import net.space.developer.meetingscheduler.domain.exception.RoomNotFoundException;
import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;
import net.space.developer.meetingscheduler.domain.port.in.CancelBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingCommand;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryBookingsUseCase;
import net.space.developer.meetingscheduler.domain.port.out.BookingRepositoryPort;
import net.space.developer.meetingscheduler.domain.port.out.RoomRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookingService implements CreateBookingUseCase, CancelBookingUseCase, QueryBookingsUseCase {

    private final RoomRepositoryPort rooms;
    private final BookingRepositoryPort bookings;

    public BookingService(RoomRepositoryPort rooms, BookingRepositoryPort bookings) {
        this.rooms = rooms;
        this.bookings = bookings;
    }

    @Override
    @Transactional
    public Booking create(CreateBookingCommand command) {
        Long roomId = command.roomId();
        rooms.findByIdForUpdate(roomId).orElseThrow(() -> new RoomNotFoundException(roomId));

        TimeSlot slot = new TimeSlot(command.startTime(), command.endTime());
        if (bookings.existsOverlapping(roomId, slot)) {
            throw new BookingConflictException(roomId, slot);
        }
        return bookings.save(Booking.create(roomId, command.personInCharge(), slot));
    }

    @Override
    @Transactional
    public void cancel(Long bookingId) {
        bookings.save(findById(bookingId).cancel());
    }

    @Override
    public Booking findById(Long id) {
        return bookings.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
    }
}
