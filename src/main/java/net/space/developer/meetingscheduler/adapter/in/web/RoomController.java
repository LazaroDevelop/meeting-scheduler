package net.space.developer.meetingscheduler.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.space.developer.meetingscheduler.adapter.in.web.dto.CreateRoomRequest;
import net.space.developer.meetingscheduler.adapter.in.web.dto.RoomResponse;
import net.space.developer.meetingscheduler.domain.port.in.CreateRoomUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryRoomsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.space.developer.meetingscheduler.adapter.in.web.common.ApiDocs;
import net.space.developer.meetingscheduler.adapter.in.web.common.Locations;

import java.util.List;

/**
 * RoomController handles HTTP requests related to rooms, including creating, searching, and retrieving rooms.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */

@Tag(name = "Rooms")
@RestController
@RequestMapping("/api/v1/rooms")
class RoomController {

    /**
     * The use case for creating a room.
     */
    private final CreateRoomUseCase createRoom;

    /**
     * The use case for querying rooms.
     */
    private final QueryRoomsUseCase queryRooms;

    /**
     * Creates a new {@code RoomController}.
     */
    RoomController(CreateRoomUseCase createRoom, QueryRoomsUseCase queryRooms) {
        this.createRoom = createRoom;
        this.queryRooms = queryRooms;
    }

    /**
     * Creates a new room based on the provided request data.
     * @param request The request data for creating a room.
     * @return A ResponseEntity containing the created room and the location of the new resource.
     */
    @Operation(summary = "Create a room", description = "Room names are unique (case-insensitive).")
    @SecurityRequirement(name = ApiDocs.BASIC_AUTH)
    @PostMapping
    ResponseEntity<RoomResponse> create(@Valid @RequestBody CreateRoomRequest request) {
        RoomResponse room = RoomResponse.from(createRoom.create(request.toCommand()));
        return ResponseEntity.created(Locations.of(room.id())).body(room);
    }

    /**
     * Retrieves a list of all rooms.
     * @return A list of RoomResponse objects representing all rooms.
     */
    @Operation(summary = "List rooms")
    @GetMapping
    List<RoomResponse> findAll() {
        return queryRooms.findAll().stream().map(RoomResponse::from).toList();
    }

    /**
     * Retrieves a room by its unique identifier.
     * @param id The unique identifier of the room to retrieve.
     * @return A RoomResponse object representing the requested room.
     */
    @Operation(summary = "Get a room")
    @GetMapping("/{id}")
    RoomResponse findById(@PathVariable Long id) {
        return RoomResponse.from(queryRooms.findById(id));
    }
}
