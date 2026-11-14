package net.space.developer.meetingscheduler.application;

import net.space.developer.meetingscheduler.TestcontainersConfiguration;
import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.BookingStatus;
import net.space.developer.meetingscheduler.domain.model.Room;
import net.space.developer.meetingscheduler.domain.port.in.BookingSearchQuery;
import net.space.developer.meetingscheduler.domain.port.in.CancelBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingCommand;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.CreateRoomCommand;
import net.space.developer.meetingscheduler.domain.port.in.CreateRoomUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryBookingsUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryRoomsUseCase;
import net.space.developer.meetingscheduler.domain.port.out.BookingRepositoryPort;
import net.space.developer.meetingscheduler.domain.port.out.RoomRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class CachingIntegrationTest {

    @Autowired
    private CreateRoomUseCase createRoom;

    @Autowired
    private QueryRoomsUseCase queryRooms;

    @Autowired
    private CreateBookingUseCase createBooking;

    @Autowired
    private CancelBookingUseCase cancelBooking;

    @Autowired
    private QueryBookingsUseCase queryBookings;

    @Autowired
    private CacheManager cacheManager;

    @MockitoSpyBean
    private RoomRepositoryPort roomRepository;

    @MockitoSpyBean
    private BookingRepositoryPort bookingRepository;

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }

    @Test
    void usesCaffeine() {
        assertThat(cacheManager).isInstanceOf(CaffeineCacheManager.class);
    }

    @Test
    void cachesRoomLookups() {
        Room room = newRoom();

        queryRooms.findById(room.id());
        queryRooms.findById(room.id());

        then(roomRepository).should(times(1)).findById(room.id());
    }

    @Test
    void evictsRoomListWhenRoomIsCreated() {
        queryRooms.findAll();
        queryRooms.findAll();
        then(roomRepository).should(times(1)).findAll();

        Room created = newRoom();

        assertThat(queryRooms.findAll()).contains(created);
        then(roomRepository).should(times(2)).findAll();
    }

    @Test
    void evictsBookingCachesWhenBookingIsCancelled() {
        Booking booking = newBooking(newRoom());
        BookingSearchQuery query = new BookingSearchQuery(booking.roomId(), LocalDate.of(2030, 1, 10), 0, 20);
        queryBookings.findById(booking.id());
        queryBookings.search(query);

        cancelBooking.cancel(booking.id());

        assertThat(queryBookings.findById(booking.id()).status()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(queryBookings.search(query).content()).singleElement()
                .extracting(Booking::status).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void evictsSearchResultsWhenBookingIsCreated() {
        Room room = newRoom();
        BookingSearchQuery query = new BookingSearchQuery(room.id(), null, 0, 20);
        queryBookings.search(query);
        queryBookings.search(query);
        then(bookingRepository).should(times(1)).search(eq(room.id()), any(), anyInt(), anyInt());

        newBooking(room);

        assertThat(queryBookings.search(query).totalElements()).isEqualTo(1);
    }

    private Room newRoom() {
        return createRoom.create(new CreateRoomCommand("Room " + UUID.randomUUID(), 6, null));
    }

    private Booking newBooking(Room room) {
        return createBooking.create(new CreateBookingCommand(room.id(), "Ada",
                Instant.parse("2030-01-10T09:00:00Z"), Instant.parse("2030-01-10T10:00:00Z")));
    }
}
