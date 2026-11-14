package net.space.developer.meetingscheduler.domain.port.in;

import net.space.developer.meetingscheduler.domain.model.Room;

public interface CreateRoomUseCase {

    Room create(CreateRoomCommand command);
}
