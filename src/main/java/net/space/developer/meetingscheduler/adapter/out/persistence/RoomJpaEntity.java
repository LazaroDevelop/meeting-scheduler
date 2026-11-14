package net.space.developer.meetingscheduler.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.space.developer.meetingscheduler.domain.model.Room;

/**
 * RoomJpaEntity is a JPA entity that represents a room in the database. It maps the Room domain model to the database table "rooms".
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */

@Entity
@Table(name = "rooms")
class RoomJpaEntity {

    /**
     * The unique identifier of the room.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the room.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * The capacity of the room.
     */
    @Column(nullable = false)
    private int capacity;

    /**
     * The location of the room.
     */
    @Column(length = 200)
    private String location;

    protected RoomJpaEntity() {
    }

    /**
     * Creates a RoomJpaEntity from a Room domain model.
     * 
     * @param room The Room domain model to convert.
     * @return The corresponding RoomJpaEntity.
     */
    static RoomJpaEntity fromDomain(Room room) {
        RoomJpaEntity entity = new RoomJpaEntity();
        entity.id = room.id();
        entity.name = room.name();
        entity.capacity = room.capacity();
        entity.location = room.location();
        return entity;
    }

    /**
     * Converts this RoomJpaEntity to a Room domain model.
     * 
     * @return The corresponding Room domain model.
     */
    Room toDomain() {
        return new Room(id, name, capacity, location);
    }
}
