package net.space.developer.meetingscheduler.application;

import net.space.developer.meetingscheduler.domain.exception.RoomAlreadyExistsException;
import net.space.developer.meetingscheduler.domain.exception.RoomNotFoundException;
import net.space.developer.meetingscheduler.domain.model.Room;
import net.space.developer.meetingscheduler.domain.port.in.CreateRoomCommand;
import net.space.developer.meetingscheduler.domain.port.in.CreateRoomUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryRoomsUseCase;
import net.space.developer.meetingscheduler.domain.port.out.RoomRepositoryPort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomService implements CreateRoomUseCase, QueryRoomsUseCase {

    private final RoomRepositoryPort rooms;

    public RoomService(RoomRepositoryPort rooms) {
        this.rooms = rooms;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CacheNames.ROOMS, allEntries = true)
    public Room create(CreateRoomCommand command) {
        if (rooms.existsByName(command.name())) {
            throw new RoomAlreadyExistsException(command.name());
        }
        return rooms.save(Room.create(command.name(), command.capacity(), command.location()));
    }

    @Override
    @Cacheable(CacheNames.ROOMS)
    public List<Room> findAll() {
        return rooms.findAll();
    }

    @Override
    @Cacheable(CacheNames.ROOM_BY_ID)
    public Room findById(Long id) {
        return rooms.findById(id).orElseThrow(() -> new RoomNotFoundException(id));
    }
}
