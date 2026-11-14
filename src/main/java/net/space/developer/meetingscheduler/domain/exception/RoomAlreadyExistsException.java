package net.space.developer.meetingscheduler.domain.exception;

public class RoomAlreadyExistsException extends ConflictException {

    public RoomAlreadyExistsException(String name) {
        super("A room named '%s' already exists".formatted(name));
    }
}
