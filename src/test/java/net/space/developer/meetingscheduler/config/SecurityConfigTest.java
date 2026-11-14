package net.space.developer.meetingscheduler.config;

import net.space.developer.meetingscheduler.domain.model.Room;
import net.space.developer.meetingscheduler.domain.port.in.CancelBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.CreateRoomUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryBookingsUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryRoomsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
class SecurityConfigTest {

    private static final String ROOM_JSON = """
            {"name": "Orion", "capacity": 8}
            """;

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
    void queriesArePublic() throws Exception {
        given(queryRooms.findAll()).willReturn(List.of());

        mvc.perform(get("/api/v1/rooms")).andExpect(status().isOk());
    }

    @Test
    void writesWithoutCredentialsAreRejectedWithProblemDetail() throws Exception {
        mvc.perform(post("/api/v1/rooms").contentType(MediaType.APPLICATION_JSON).content(ROOM_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("WWW-Authenticate"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void bookingWritesRequireAuthentication() throws Exception {
        mvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/v1/bookings/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        mvc.perform(post("/api/v1/rooms").with(httpBasic("admin", "wrong"))
                        .contentType(MediaType.APPLICATION_JSON).content(ROOM_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void configuredUserCanWrite() throws Exception {
        given(createRoom.create(any())).willReturn(new Room(1L, "Orion", 8, null));

        mvc.perform(post("/api/v1/rooms").with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON).content(ROOM_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void actuatorEndpointsOtherThanHealthRequireAuthentication() throws Exception {
        mvc.perform(get("/actuator/metrics")).andExpect(status().isUnauthorized());
    }
}
