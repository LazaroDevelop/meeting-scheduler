package net.space.developer.meetingscheduler.adapter.out.persistence;

import net.space.developer.meetingscheduler.adapter.out.persistence.annotations.RoomAdapter;
import net.space.developer.meetingscheduler.domain.model.Room;
import net.space.developer.meetingscheduler.domain.port.out.RoomRepositoryPort;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief RoomPersistenceAdapter is a persistence adapter for managing room entities.
 */

@RoomAdapter
class RoomPersistenceAdapter implements RoomRepositoryPort {
    
    private final RoomJpaRepository repository;

    /**
     * Copy constructor for RoomPersistenceAdapter.
     */
    RoomPersistenceAdapter(RoomJpaRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Room save(Room room) {
        return repository.save(RoomJpaEntity.fromDomain(room)).toDomain();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Room> findById(Long id) {
        return repository.findById(id).map(RoomJpaEntity::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Room> findByIdForUpdate(Long id) {
        return repository.findByIdForUpdate(id).map(RoomJpaEntity::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Room> findAll() {
        return repository.findAll(Sort.by("name")).stream().map(RoomJpaEntity::toDomain).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByName(String name) {
        return repository.existsByNameIgnoreCase(name);
    }
}
