package net.space.developer.meetingscheduler.domain.exception;

/**
 * Exception thrown when attempting to create a room that already exists in the system.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public class RoomAlreadyExistsException extends ConflictException {

    /**
     * Constructs a new RoomAlreadyExistsException with a message indicating the room name that already exists.
     * 
     * @param name the name of the room that already exists
     */
    public RoomAlreadyExistsException(String name) {
        super("A room named '%s' already exists".formatted(name));
    }
}
