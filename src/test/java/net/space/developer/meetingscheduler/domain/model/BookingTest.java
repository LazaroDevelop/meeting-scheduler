package net.space.developer.meetingscheduler.domain.model;

import net.space.developer.meetingscheduler.domain.exception.BookingAlreadyCancelledException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingTest {

    private static final TimeSlot SLOT = new TimeSlot(
            Instant.parse("2030-01-10T09:00:00Z"), Instant.parse("2030-01-10T10:00:00Z"));

    @Test
    void newBookingIsActiveAndHasNoId() {
        Booking booking = Booking.create(1L, "Ada Lovelace", SLOT);

        assertThat(booking.id()).isNull();
        assertThat(booking.roomId()).isEqualTo(1L);
        assertThat(booking.personInCharge()).isEqualTo("Ada Lovelace");
        assertThat(booking.slot()).isEqualTo(SLOT);
        assertThat(booking.status()).isEqualTo(BookingStatus.ACTIVE);
        assertThat(booking.isActive()).isTrue();
    }

    @Test
    void cancelReturnsCancelledCopy() {
        Booking active = new Booking(5L, 1L, "Ada Lovelace", SLOT, BookingStatus.ACTIVE);

        Booking cancelled = active.cancel();

        assertThat(cancelled.status()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(cancelled.isActive()).isFalse();
        assertThat(cancelled.id()).isEqualTo(5L);
        assertThat(active.status()).isEqualTo(BookingStatus.ACTIVE);
    }

    @Test
    void cancellingTwiceFails() {
        Booking cancelled = new Booking(5L, 1L, "Ada Lovelace", SLOT, BookingStatus.CANCELLED);

        assertThatThrownBy(cancelled::cancel)
                .isInstanceOf(BookingAlreadyCancelledException.class)
                .hasMessageContaining("5");
    }
}
