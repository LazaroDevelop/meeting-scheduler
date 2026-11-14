package net.space.developer.meetingscheduler.domain.exception;

public class RoomNotFoundException extends ResourceNotFoundException {

    public RoomNotFoundException(Long id) {
        super("Room", id);
    }
}
