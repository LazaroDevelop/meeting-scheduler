package net.space.developer.meetingscheduler.domain.port.in;

/**
 * CreateRoomCommand is a command object for creating a new room.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public record CreateRoomCommand(String name, int capacity, String location) {
}
