package net.space.developer.meetingscheduler.domain.port.in;

import net.space.developer.meetingscheduler.domain.model.Room;

import java.util.List;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief QueryRoomsUseCase is a use case interface for querying room information.
 */

public interface QueryRoomsUseCase {

    /**
     * Finds all rooms in the system.
     * 
     * @return a list of all rooms
     */
    List<Room> findAll();

    /**
     * Finds a room by its unique identifier.
     * 
     * @param id the unique identifier of the room
     * @return the room with the specified id
     */
    Room findById(Long id);
}
