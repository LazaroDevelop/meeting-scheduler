package net.space.developer.meetingscheduler.application;

import net.space.developer.meetingscheduler.domain.exception.BookingAlreadyCancelledException;
import net.space.developer.meetingscheduler.domain.exception.BookingConflictException;
import net.space.developer.meetingscheduler.domain.exception.BookingNotFoundException;
import net.space.developer.meetingscheduler.domain.exception.RoomNotFoundException;
import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.BookingStatus;
import net.space.developer.meetingscheduler.domain.model.Room;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingCommand;
import net.space.developer.meetingscheduler.domain.port.out.BookingRepositoryPort;
import net.space.developer.meetingscheduler.domain.port.out.RoomRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private static final Instant NINE = Instant.parse("2030-01-10T09:00:00Z");
    private static final Instant TEN = Instant.parse("2030-01-10T10:00:00Z");
    private static final TimeSlot SLOT = new TimeSlot(NINE, TEN);
    private static final Room ROOM = new Room(1L, "Orion", 8, null);
    private static final CreateBookingCommand COMMAND = new CreateBookingCommand(1L, "Ada Lovelace", NINE, TEN);

    @Mock
    private RoomRepositoryPort rooms;

    @Mock
    private BookingRepositoryPort bookings;

    @InjectMocks
    private BookingService service;

    @Test
    void booksFreeSlot() {
        given(rooms.findByIdForUpdate(1L)).willReturn(Optional.of(ROOM));
        given(bookings.existsOverlapping(1L, SLOT)).willReturn(false);
        given(bookings.save(any())).willAnswer(call -> withId(call.getArgument(0), 7L));

        Booking booking = service.create(COMMAND);

        assertThat(booking).isEqualTo(new Booking(7L, 1L, "Ada Lovelace", SLOT, BookingStatus.ACTIVE));
    }

    @Test
    void rejectsBookingForUnknownRoom() {
        given(rooms.findByIdForUpdate(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(COMMAND)).isInstanceOf(RoomNotFoundException.class);
        then(bookings).shouldHaveNoInteractions();
    }

    @Test
    void rejectsOverlappingBooking() {
        given(rooms.findByIdForUpdate(1L)).willReturn(Optional.of(ROOM));
        given(bookings.existsOverlapping(1L, SLOT)).willReturn(true);

        assertThatThrownBy(() -> service.create(COMMAND)).isInstanceOf(BookingConflictException.class);
        then(bookings).should(never()).save(any());
    }

    @Test
    void findsBookingById() {
        Booking booking = new Booking(7L, 1L, "Ada Lovelace", SLOT, BookingStatus.ACTIVE);
        given(bookings.findById(7L)).willReturn(Optional.of(booking));

        assertThat(service.findById(7L)).isEqualTo(booking);
    }

    @Test
    void failsWhenBookingDoesNotExist() {
        given(bookings.findById(7L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(7L)).isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void cancelsActiveBooking() {
        given(bookings.findById(7L))
                .willReturn(Optional.of(new Booking(7L, 1L, "Ada Lovelace", SLOT, BookingStatus.ACTIVE)));

        service.cancel(7L);

        then(bookings).should().save(new Booking(7L, 1L, "Ada Lovelace", SLOT, BookingStatus.CANCELLED));
    }

    @Test
    void cannotCancelTwice() {
        given(bookings.findById(7L))
                .willReturn(Optional.of(new Booking(7L, 1L, "Ada Lovelace", SLOT, BookingStatus.CANCELLED)));

        assertThatThrownBy(() -> service.cancel(7L)).isInstanceOf(BookingAlreadyCancelledException.class);
        then(bookings).should(never()).save(any());
    }

    @Test
    void cannotCancelUnknownBooking() {
        given(bookings.findById(7L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(7L)).isInstanceOf(BookingNotFoundException.class);
    }

    private static Booking withId(Booking booking, Long id) {
        return new Booking(id, booking.roomId(), booking.personInCharge(), booking.slot(), booking.status());
    }
}
