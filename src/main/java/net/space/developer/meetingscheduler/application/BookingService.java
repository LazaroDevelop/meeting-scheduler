package net.space.developer.meetingscheduler.application;

import net.space.developer.meetingscheduler.domain.exception.BookingConflictException;
import net.space.developer.meetingscheduler.domain.exception.BookingNotFoundException;
import net.space.developer.meetingscheduler.domain.exception.RoomNotFoundException;
import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.PageResult;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;
import net.space.developer.meetingscheduler.domain.port.in.BookingSearchQuery;
import net.space.developer.meetingscheduler.domain.port.in.CancelBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingCommand;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryBookingsUseCase;
import net.space.developer.meetingscheduler.domain.port.out.BookingRepositoryPort;
import net.space.developer.meetingscheduler.domain.port.out.RoomRepositoryPort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;

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
    @CacheEvict(cacheNames = CacheNames.BOOKING_SEARCH, allEntries = true)
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
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.BOOKING_BY_ID, key = "#bookingId"),
            @CacheEvict(cacheNames = CacheNames.BOOKING_SEARCH, allEntries = true)
    })
    public void cancel(Long bookingId) {
        bookings.save(load(bookingId).cancel());
    }

    @Override
    @Cacheable(CacheNames.BOOKING_BY_ID)
    public Booking findById(Long id) {
        return load(id);
    }

    @Override
    @Cacheable(CacheNames.BOOKING_SEARCH)
    public PageResult<Booking> search(BookingSearchQuery query) {
        TimeSlot window = query.date() == null ? null : utcDay(query.date());
        return bookings.search(query.roomId(), window, query.page(), query.size());
    }

    private Booking load(Long id) {
        return bookings.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
    }

    private static TimeSlot utcDay(LocalDate date) {
        return new TimeSlot(date.atStartOfDay(ZoneOffset.UTC).toInstant(),
                date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant());
    }
}
