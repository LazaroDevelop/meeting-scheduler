package net.space.developer.meetingscheduler.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.space.developer.meetingscheduler.domain.model.Room;

@Entity
@Table(name = "rooms")
class RoomJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @Column(length = 200)
    private String location;

    protected RoomJpaEntity() {
    }

    static RoomJpaEntity fromDomain(Room room) {
        RoomJpaEntity entity = new RoomJpaEntity();
        entity.id = room.id();
        entity.name = room.name();
        entity.capacity = room.capacity();
        entity.location = room.location();
        return entity;
    }

    Room toDomain() {
        return new Room(id, name, capacity, location);
    }
}
