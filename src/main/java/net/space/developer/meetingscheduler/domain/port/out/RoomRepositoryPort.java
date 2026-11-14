package net.space.developer.meetingscheduler.domain.port.out;

import net.space.developer.meetingscheduler.domain.model.Room;

import java.util.List;
import java.util.Optional;

/**
 * RoomRepositoryPort is the interface that defines the contract for room repository operations in the domain layer.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public interface RoomRepositoryPort {

    /**
     * Saves a room to the repository.
     * @param room - The room object to be saved.
     * @return The saved room object, potentially with updated fields (e.g., ID).
     */
    Room save(Room room);

    /**
     * Finds a room by its unique identifier.
     * @param id - The unique identifier of the room.
     * @return An Optional containing the Room if found, or empty if not found.
     */
    Optional<Room> findById(Long id);

    /**
     * Loads the room holding an exclusive lock until the current transaction ends,
     * serializing concurrent bookings of the same room.
     */
    Optional<Room> findByIdForUpdate(Long id);

    /**
     * Finds all rooms in the repository.
     * @return A list of all Room objects in the repository.
     */
    List<Room> findAll();

    /**
     * Checks if a room with the given name exists in the repository.
     * 
     * @param name - The name of the room to check.
     * @return true if a room with the given name exists, false otherwise.
     */
    boolean existsByName(String name);
}
