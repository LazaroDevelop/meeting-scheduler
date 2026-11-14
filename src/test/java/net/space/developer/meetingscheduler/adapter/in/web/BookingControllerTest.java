package net.space.developer.meetingscheduler.adapter.in.web;

import net.space.developer.meetingscheduler.domain.exception.BookingAlreadyCancelledException;
import net.space.developer.meetingscheduler.domain.exception.BookingConflictException;
import net.space.developer.meetingscheduler.domain.exception.BookingNotFoundException;
import net.space.developer.meetingscheduler.domain.exception.RoomNotFoundException;
import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.BookingStatus;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;
import net.space.developer.meetingscheduler.domain.port.in.CancelBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingCommand;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryBookingsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String BOOKINGS = "/api/v1/bookings";
    private static final Instant NINE = Instant.parse("2030-01-10T09:00:00Z");
    private static final Instant TEN = Instant.parse("2030-01-10T10:00:00Z");
    private static final CreateBookingCommand COMMAND = new CreateBookingCommand(1L, "Ada Lovelace", NINE, TEN);
    private static final Booking BOOKING = new Booking(7L, 1L, "Ada Lovelace", new TimeSlot(NINE, TEN), BookingStatus.ACTIVE);

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CreateBookingUseCase createBooking;

    @MockitoBean
    private CancelBookingUseCase cancelBooking;

    @MockitoBean
    private QueryBookingsUseCase queryBookings;

    @Test
    void createsBooking() throws Exception {
        given(createBooking.create(COMMAND)).willReturn(BOOKING);

        postBooking(validBody())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/bookings/7"))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.roomId").value(1))
                .andExpect(jsonPath("$.personInCharge").value("Ada Lovelace"))
                .andExpect(jsonPath("$.startTime").value("2030-01-10T09:00:00Z"))
                .andExpect(jsonPath("$.endTime").value("2030-01-10T10:00:00Z"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void rejectsMissingFields() throws Exception {
        postBooking("{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].field", hasItems("roomId", "personInCharge", "startTime", "endTime")));
    }

    @Test
    void rejectsEndBeforeStart() throws Exception {
        postBooking("""
                {"roomId": 1, "personInCharge": "Ada", "startTime": "2030-01-10T10:00:00Z", "endTime": "2030-01-10T09:00:00Z"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].field", hasItem("endTime")));
    }

    @Test
    void rejectsStartInThePast() throws Exception {
        postBooking("""
                {"roomId": 1, "personInCharge": "Ada", "startTime": "2000-01-10T09:00:00Z", "endTime": "2000-01-10T10:00:00Z"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].field", hasItem("startTime")));
    }

    @Test
    void returnsConflictWhenRoomIsOccupied() throws Exception {
        given(createBooking.create(COMMAND)).willThrow(new BookingConflictException(1L, new TimeSlot(NINE, TEN)));

        postBooking(validBody())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"));
    }

    @Test
    void returnsNotFoundForUnknownRoom() throws Exception {
        given(createBooking.create(COMMAND)).willThrow(new RoomNotFoundException(1L));

        postBooking(validBody()).andExpect(status().isNotFound());
    }

    @Test
    void getsBookingById() throws Exception {
        given(queryBookings.findById(7L)).willReturn(BOOKING);

        mvc.perform(get(BOOKINGS + "/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personInCharge").value("Ada Lovelace"));
    }

    @Test
    void returnsNotFoundForUnknownBooking() throws Exception {
        given(queryBookings.findById(7L)).willThrow(new BookingNotFoundException(7L));

        mvc.perform(get(BOOKINGS + "/7")).andExpect(status().isNotFound());
    }

    @Test
    void cancelsBooking() throws Exception {
        mvc.perform(delete(BOOKINGS + "/7")).andExpect(status().isNoContent());

        then(cancelBooking).should().cancel(7L);
    }

    @Test
    void returnsConflictWhenAlreadyCancelled() throws Exception {
        willThrow(new BookingAlreadyCancelledException(7L)).given(cancelBooking).cancel(7L);

        mvc.perform(delete(BOOKINGS + "/7"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Booking 7 is already cancelled"));
    }

    private ResultActions postBooking(String body) throws Exception {
        return mvc.perform(post(BOOKINGS).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private static String validBody() {
        return """
                {"roomId": 1, "personInCharge": "Ada Lovelace", "startTime": "2030-01-10T09:00:00Z", "endTime": "2030-01-10T10:00:00Z"}
                """;
    }
}
