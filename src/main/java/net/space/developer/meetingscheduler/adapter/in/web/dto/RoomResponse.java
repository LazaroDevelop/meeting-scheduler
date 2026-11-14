package net.space.developer.meetingscheduler.adapter.in.web.dto;

import net.space.developer.meetingscheduler.domain.model.Room;

public record RoomResponse(Long id, String name, int capacity, String location) {

    public static RoomResponse from(Room room) {
        return new RoomResponse(room.id(), room.name(), room.capacity(), room.location());
    }
}
