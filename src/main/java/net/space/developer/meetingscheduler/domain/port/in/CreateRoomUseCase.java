package net.space.developer.meetingscheduler.domain.port.in;

import net.space.developer.meetingscheduler.domain.model.Room;

/**
 * CreateRoomUseCase is a use case interface for creating room information.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public interface CreateRoomUseCase {

    /**
     * Creates a new room based on the provided command.
     * 
     * @param command the command containing room creation details
     * @return the created room
     */
    Room create(CreateRoomCommand command);
}
