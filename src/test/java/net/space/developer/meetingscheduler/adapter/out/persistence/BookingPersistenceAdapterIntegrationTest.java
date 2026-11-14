package net.space.developer.meetingscheduler.adapter.out.persistence;

import net.space.developer.meetingscheduler.TestcontainersConfiguration;
import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.PageResult;
import net.space.developer.meetingscheduler.domain.model.Room;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, RoomPersistenceAdapter.class, BookingPersistenceAdapter.class})
class BookingPersistenceAdapterIntegrationTest {

    @Autowired
    private RoomPersistenceAdapter rooms;

    @Autowired
    private BookingPersistenceAdapter bookings;

    private Long roomId;

    @BeforeEach
    void setUp() {
        roomId = rooms.save(Room.create("Orion", 8, null)).id();
        bookings.save(Booking.create(roomId, "Ada", slot("09:00", "10:00")));
    }

    @Test
    void savesAndReadsBooking() {
        Booking saved = bookings.save(Booking.create(roomId, "Grace", slot("11:00", "12:00")));

        assertThat(saved.id()).isNotNull();
        assertThat(bookings.findById(saved.id())).contains(saved);
    }

    @Test
    void detectsOverlapWithActiveBooking() {
        assertThat(bookings.existsOverlapping(roomId, slot("09:30", "10:30"))).isTrue();
    }

    @Test
    void allowsBackToBackBookings() {
        assertThat(bookings.existsOverlapping(roomId, slot("10:00", "11:00"))).isFalse();
        assertThat(bookings.existsOverlapping(roomId, slot("08:00", "09:00"))).isFalse();
    }

    @Test
    void ignoresCancelledBookings() {
        Booking other = bookings.save(Booking.create(roomId, "Grace", slot("13:00", "14:00")));
        bookings.save(other.cancel());

        assertThat(bookings.existsOverlapping(roomId, slot("13:00", "14:00"))).isFalse();
    }

    @Test
    void ignoresOtherRooms() {
        Long otherRoom = rooms.save(Room.create("Vega", 4, null)).id();

        assertThat(bookings.existsOverlapping(otherRoom, slot("09:00", "10:00"))).isFalse();
    }

    @Test
    void searchesByRoomAndDayWindowOrderedByStart() {
        Long otherRoom = rooms.save(Room.create("Vega", 4, null)).id();
        bookings.save(Booking.create(roomId, "Grace", slot("07:00", "08:00")));
        bookings.save(Booking.create(otherRoom, "Linus", slot("09:00", "10:00")));
        bookings.save(Booking.create(roomId, "Next day", new TimeSlot(
                Instant.parse("2030-01-11T09:00:00Z"), Instant.parse("2030-01-11T10:00:00Z"))));
        TimeSlot day = new TimeSlot(Instant.parse("2030-01-10T00:00:00Z"), Instant.parse("2030-01-11T00:00:00Z"));

        PageResult<Booking> page = bookings.search(roomId, day, 0, 10);

        assertThat(page.content()).extracting(Booking::personInCharge).containsExactly("Grace", "Ada");
        assertThat(page.totalElements()).isEqualTo(2);
    }

    @Test
    void paginatesUnfilteredSearch() {
        bookings.save(Booking.create(roomId, "Grace", slot("11:00", "12:00")));
        bookings.save(Booking.create(roomId, "Linus", slot("13:00", "14:00")));

        PageResult<Booking> page = bookings.search(null, null, 1, 2);

        assertThat(page.content()).extracting(Booking::personInCharge).containsExactly("Linus");
        assertThat(page.page()).isEqualTo(1);
        assertThat(page.totalElements()).isEqualTo(3);
        assertThat(page.totalPages()).isEqualTo(2);
    }

    @Test
    void locksRoomForUpdate() {
        assertThat(rooms.findByIdForUpdate(roomId)).map(Room::name).contains("Orion");
    }

    private static TimeSlot slot(String start, String end) {
        return new TimeSlot(Instant.parse("2030-01-10T" + start + ":00Z"), Instant.parse("2030-01-10T" + end + ":00Z"));
    }
}
