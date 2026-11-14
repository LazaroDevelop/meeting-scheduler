package net.space.developer.meetingscheduler.adapter.in.web;

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

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
class RoomController {

    private final CreateRoomUseCase createRoom;
    private final QueryRoomsUseCase queryRooms;

    RoomController(CreateRoomUseCase createRoom, QueryRoomsUseCase queryRooms) {
        this.createRoom = createRoom;
        this.queryRooms = queryRooms;
    }

    @PostMapping
    ResponseEntity<RoomResponse> create(@Valid @RequestBody CreateRoomRequest request) {
        RoomResponse room = RoomResponse.from(createRoom.create(request.toCommand()));
        return ResponseEntity.created(Locations.of(room.id())).body(room);
    }

    @GetMapping
    List<RoomResponse> findAll() {
        return queryRooms.findAll().stream().map(RoomResponse::from).toList();
    }

    @GetMapping("/{id}")
    RoomResponse findById(@PathVariable Long id) {
        return RoomResponse.from(queryRooms.findById(id));
    }
}
