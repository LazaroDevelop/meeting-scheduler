package net.space.developer.meetingscheduler.domain.port.in;

import net.space.developer.meetingscheduler.domain.model.Room;

import java.util.List;

public interface QueryRoomsUseCase {

    List<Room> findAll();

    Room findById(Long id);
}
