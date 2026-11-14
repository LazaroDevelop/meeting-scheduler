package net.space.developer.meetingscheduler.application;

import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.PageResult;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;
import net.space.developer.meetingscheduler.domain.port.in.BookingSearchQuery;
import net.space.developer.meetingscheduler.domain.port.out.BookingRepositoryPort;
import net.space.developer.meetingscheduler.domain.port.out.RoomRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookingSearchTest {

    private static final PageResult<Booking> EMPTY = new PageResult<>(List.of(), 0, 20, 0);

    @Mock
    private RoomRepositoryPort rooms;

    @Mock
    private BookingRepositoryPort bookings;

    @InjectMocks
    private BookingService service;

    @Test
    void translatesDateIntoWholeUtcDayWindow() {
        TimeSlot day = new TimeSlot(Instant.parse("2030-01-10T00:00:00Z"), Instant.parse("2030-01-11T00:00:00Z"));
        given(bookings.search(1L, day, 0, 20)).willReturn(EMPTY);

        PageResult<Booking> result = service.search(new BookingSearchQuery(1L, LocalDate.of(2030, 1, 10), 0, 20));

        assertThat(result).isSameAs(EMPTY);
    }

    @Test
    void searchesWithoutWindowWhenNoDateGiven() {
        given(bookings.search(null, null, 2, 5)).willReturn(EMPTY);

        assertThat(service.search(new BookingSearchQuery(null, null, 2, 5))).isSameAs(EMPTY);
    }

    @Test
    void computesTotalPages() {
        assertThat(new PageResult<>(List.of(), 0, 20, 0).totalPages()).isZero();
        assertThat(new PageResult<>(List.of(), 0, 20, 20).totalPages()).isEqualTo(1);
        assertThat(new PageResult<>(List.of(), 0, 20, 21).totalPages()).isEqualTo(2);
    }
}
