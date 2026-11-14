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

/**
 * Application service implementing the room use cases: creation, listing and lookup by id.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */

@Service
@Transactional(readOnly = true)
public class RoomService implements CreateRoomUseCase, QueryRoomsUseCase {

    /**
     * The repository port for managing room entities.
     */
    private final RoomRepositoryPort rooms;

    /**
     * Creates a new {@code RoomService}.
     */
    public RoomService(RoomRepositoryPort rooms) {
        this.rooms = rooms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = CacheNames.ROOMS, allEntries = true)
    public Room create(CreateRoomCommand command) {
        if (rooms.existsByName(command.name())) {
            throw new RoomAlreadyExistsException(command.name());
        }
        return rooms.save(Room.create(command.name(), command.capacity(), command.location()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(CacheNames.ROOMS)
    public List<Room> findAll() {
        return rooms.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(CacheNames.ROOM_BY_ID)
    public Room findById(Long id) {
        return rooms.findById(id).orElseThrow(() -> new RoomNotFoundException(id));
    }
}
