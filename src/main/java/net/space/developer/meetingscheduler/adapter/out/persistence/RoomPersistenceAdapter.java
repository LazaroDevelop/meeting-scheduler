package net.space.developer.meetingscheduler.adapter.out.persistence;

import net.space.developer.meetingscheduler.domain.model.Room;
import net.space.developer.meetingscheduler.domain.port.out.RoomRepositoryPort;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class RoomPersistenceAdapter implements RoomRepositoryPort {

    private final RoomJpaRepository repository;

    RoomPersistenceAdapter(RoomJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Room save(Room room) {
        return repository.save(RoomJpaEntity.fromDomain(room)).toDomain();
    }

    @Override
    public Optional<Room> findById(Long id) {
        return repository.findById(id).map(RoomJpaEntity::toDomain);
    }

    @Override
    public Optional<Room> findByIdForUpdate(Long id) {
        return repository.findByIdForUpdate(id).map(RoomJpaEntity::toDomain);
    }

    @Override
    public List<Room> findAll() {
        return repository.findAll(Sort.by("name")).stream().map(RoomJpaEntity::toDomain).toList();
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByNameIgnoreCase(name);
    }
}
