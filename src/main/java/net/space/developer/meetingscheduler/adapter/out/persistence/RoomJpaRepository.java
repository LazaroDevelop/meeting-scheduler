package net.space.developer.meetingscheduler.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief JPA repository for managing room entities.
 */

interface RoomJpaRepository extends JpaRepository<RoomJpaEntity, Long> {

    /**
     * Checks if a room with the specified name exists, ignoring case sensitivity.
     * 
     * @param name - The name of the room to check for existence.
     * @return true if a room with the specified name exists, false otherwise.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Finds a room by its unique identifier and applies a pessimistic write lock to the entity.
     * 
     * @param id - The unique identifier of the room to find.
     * @return An Optional containing the room if found, or empty if not found.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RoomJpaEntity r where r.id = :id")
    Optional<RoomJpaEntity> findByIdForUpdate(Long id);
}
