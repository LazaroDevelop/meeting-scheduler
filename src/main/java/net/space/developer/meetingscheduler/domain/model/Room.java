package net.space.developer.meetingscheduler.domain.model;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @param id - The unique identifier of the room. It can be null when creating a new room.
 * @param name - The name of the room.
 * @param capacity - The maximum number of people that the room can accommodate.
 * @param location - The physical location of the room.
 */
public record Room(Long id, String name, int capacity, String location) {

    public static Room create(String name, int capacity, String location) {
        return new Room(null, name, capacity, location);
    }
}
