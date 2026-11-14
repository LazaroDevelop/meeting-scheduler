package net.space.developer.meetingscheduler.domain.port.in;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief CreateRoomCommand is a command object for creating a new room.
 */
public record CreateRoomCommand(String name, int capacity, String location) {
}
