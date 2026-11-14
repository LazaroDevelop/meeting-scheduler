package net.space.developer.meetingscheduler.domain.port.out;

import net.space.developer.meetingscheduler.domain.model.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepositoryPort {

    Room save(Room room);

    Optional<Room> findById(Long id);

    List<Room> findAll();

    boolean existsByName(String name);
}
