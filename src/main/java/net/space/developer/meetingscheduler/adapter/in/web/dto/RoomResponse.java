package net.space.developer.meetingscheduler.adapter.in.web.dto;

import net.space.developer.meetingscheduler.domain.model.Room;

/**
 * RoomResponse is the response DTO for a room.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * @param id - The unique identifier of the room.
 * @param name - The name of the room.
 * @param capacity - The maximum number of people that can be accommodated in the room.
 * @param location - The location of the room.
 */
public record RoomResponse(Long id, String name, int capacity, String location) {

    public static RoomResponse from(Room room) {
        return new RoomResponse(room.id(), room.name(), room.capacity(), room.location());
    }
}
