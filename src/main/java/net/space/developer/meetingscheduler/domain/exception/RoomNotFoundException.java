package net.space.developer.meetingscheduler.domain.exception;

/**
 * Exception thrown when a room is not found in the system
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public class RoomNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs a new RoomNotFoundException with a message indicating the room ID that was not found.
     * 
     * @param id the ID of the room that was not found
     */
    public RoomNotFoundException(Long id) {
        super("Room", id);
    }
}
