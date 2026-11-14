package net.space.developer.meetingscheduler.domain.model;

import net.space.developer.meetingscheduler.domain.exception.InvalidTimeRangeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeSlotTest {

    private static final Instant NINE = Instant.parse("2030-01-10T09:00:00Z");
    private static final Instant TEN = Instant.parse("2030-01-10T10:00:00Z");

    @Test
    void createsSlotWhenEndIsAfterStart() {
        TimeSlot slot = new TimeSlot(NINE, TEN);

        assertThat(slot.start()).isEqualTo(NINE);
        assertThat(slot.end()).isEqualTo(TEN);
    }

    @Test
    void rejectsEndEqualToStart() {
        assertThatThrownBy(() -> new TimeSlot(NINE, NINE))
                .isInstanceOf(InvalidTimeRangeException.class)
                .hasMessageContaining("after");
    }

    @Test
    void rejectsEndBeforeStart() {
        assertThatThrownBy(() -> new TimeSlot(TEN, NINE))
                .isInstanceOf(InvalidTimeRangeException.class);
    }

    @Test
    void rejectsMissingBounds() {
        assertThatThrownBy(() -> new TimeSlot(null, TEN)).isInstanceOf(InvalidTimeRangeException.class);
        assertThatThrownBy(() -> new TimeSlot(NINE, null)).isInstanceOf(InvalidTimeRangeException.class);
    }

    @ParameterizedTest(name = "[{0} - {1}) overlaps [09:00 - 10:00) = {2}")
    @CsvSource({
            "08:00, 09:00, false",  // touching before
            "10:00, 11:00, false",  // touching after
            "08:30, 09:30, true",   // overlaps start
            "09:30, 10:30, true",   // overlaps end
            "09:15, 09:45, true",   // inside
            "08:00, 11:00, true",   // contains
            "09:00, 10:00, true",   // identical
            "06:00, 07:00, false"   // disjoint
    })
    void detectsOverlaps(String start, String end, boolean expected) {
        TimeSlot base = new TimeSlot(NINE, TEN);
        TimeSlot other = new TimeSlot(at(start), at(end));

        assertThat(base.overlaps(other)).isEqualTo(expected);
        assertThat(other.overlaps(base)).isEqualTo(expected);
    }

    private static Instant at(String time) {
        return Instant.parse("2030-01-10T" + time + ":00Z");
    }
}
