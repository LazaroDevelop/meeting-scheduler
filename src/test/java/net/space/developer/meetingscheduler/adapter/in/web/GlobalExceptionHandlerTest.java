package net.space.developer.meetingscheduler.adapter.in.web;

import net.space.developer.meetingscheduler.domain.exception.InvalidTimeRangeException;
import net.space.developer.meetingscheduler.domain.port.in.CancelBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.CreateRoomUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryBookingsUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryRoomsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({RoomController.class, BookingController.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CreateRoomUseCase createRoom;

    @MockitoBean
    private QueryRoomsUseCase queryRooms;

    @MockitoBean
    private CreateBookingUseCase createBooking;

    @MockitoBean
    private CancelBookingUseCase cancelBooking;

    @MockitoBean
    private QueryBookingsUseCase queryBookings;

    @Test
    void reportsMalformedJson() throws Exception {
        mvc.perform(post("/api/v1/rooms").contentType(MediaType.APPLICATION_JSON).content("{\"name\": "))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Malformed request"));
    }

    @Test
    void reportsWrongParameterType() throws Exception {
        mvc.perform(get("/api/v1/bookings").param("date", "10-01-2030"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors[*].field", hasItem("date")));
    }

    @Test
    void reportsWrongPathVariableType() throws Exception {
        mvc.perform(get("/api/v1/rooms/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].field", hasItem("id")));
    }

    @Test
    void reportsDomainTimeRangeViolation() throws Exception {
        given(createBooking.create(any())).willThrow(new InvalidTimeRangeException("End time must be after start time"));

        mvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON).content("""
                        {"roomId": 1, "personInCharge": "Ada", "startTime": "2030-01-10T09:00:00Z", "endTime": "2030-01-10T10:00:00Z"}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("End time must be after start time"));
    }

    @Test
    void mapsDataIntegrityViolationToConflict() throws Exception {
        given(createRoom.create(any())).willThrow(new DataIntegrityViolationException("duplicate key uk_rooms_name"));

        mvc.perform(post("/api/v1/rooms").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "Orion", "capacity": 8}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail", not(containsString("uk_rooms_name"))));
    }

    @Test
    void hidesUnexpectedErrors() throws Exception {
        given(queryRooms.findAll()).willThrow(new IllegalStateException("database password is hunter2"));

        mvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Internal error"))
                .andExpect(jsonPath("$.detail", not(containsString("hunter2"))));
    }

    @Test
    void keepsFrameworkStatusForUnsupportedMethod() throws Exception {
        mvc.perform(put("/api/v1/rooms"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }
}
