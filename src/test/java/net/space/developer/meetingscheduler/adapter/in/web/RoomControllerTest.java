package net.space.developer.meetingscheduler.adapter.in.web;

import net.space.developer.meetingscheduler.domain.exception.RoomAlreadyExistsException;
import net.space.developer.meetingscheduler.domain.exception.RoomNotFoundException;
import net.space.developer.meetingscheduler.domain.model.Room;
import net.space.developer.meetingscheduler.domain.port.in.CreateRoomCommand;
import net.space.developer.meetingscheduler.domain.port.in.CreateRoomUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryRoomsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    private static final String ROOMS = "/api/v1/rooms";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CreateRoomUseCase createRoom;

    @MockitoBean
    private QueryRoomsUseCase queryRooms;

    @Test
    void createsRoom() throws Exception {
        given(createRoom.create(new CreateRoomCommand("Orion", 8, "2nd floor")))
                .willReturn(new Room(1L, "Orion", 8, "2nd floor"));

        mvc.perform(post(ROOMS).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Orion", "capacity": 8, "location": "2nd floor"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/rooms/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Orion"));
    }

    @Test
    void rejectsInvalidRoom() throws Exception {
        mvc.perform(post(ROOMS).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": " ", "capacity": 0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors[*].field", hasItems("name", "capacity")));
    }

    @Test
    void returnsConflictForDuplicateName() throws Exception {
        given(createRoom.create(new CreateRoomCommand("Orion", 8, null)))
                .willThrow(new RoomAlreadyExistsException("Orion"));

        mvc.perform(post(ROOMS).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Orion", "capacity": 8}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("A room named 'Orion' already exists"));
    }

    @Test
    void listsRooms() throws Exception {
        given(queryRooms.findAll()).willReturn(List.of(new Room(1L, "Orion", 8, null)));

        mvc.perform(get(ROOMS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Orion"));
    }

    @Test
    void getsRoomById() throws Exception {
        given(queryRooms.findById(1L)).willReturn(new Room(1L, "Orion", 8, null));

        mvc.perform(get(ROOMS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(8));
    }

    @Test
    void returnsNotFoundForUnknownRoom() throws Exception {
        given(queryRooms.findById(9L)).willThrow(new RoomNotFoundException(9L));

        mvc.perform(get(ROOMS + "/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Room with id 9 was not found"));
    }
}
