package net.space.developer.meetingscheduler.adapter.out.persistence;

import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.BookingStatus;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;
import net.space.developer.meetingscheduler.domain.port.out.BookingRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class BookingPersistenceAdapter implements BookingRepositoryPort {

    private final BookingJpaRepository repository;

    BookingPersistenceAdapter(BookingJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Booking save(Booking booking) {
        return repository.save(BookingJpaEntity.fromDomain(booking)).toDomain();
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return repository.findById(id).map(BookingJpaEntity::toDomain);
    }

    @Override
    public boolean existsOverlapping(Long roomId, TimeSlot slot) {
        return repository.existsOverlapping(roomId, BookingStatus.ACTIVE, slot.start(), slot.end());
    }
}
