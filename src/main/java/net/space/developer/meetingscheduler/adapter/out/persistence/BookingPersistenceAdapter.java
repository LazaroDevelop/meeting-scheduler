package net.space.developer.meetingscheduler.adapter.out.persistence;

import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.BookingStatus;
import net.space.developer.meetingscheduler.domain.model.PageResult;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;
import net.space.developer.meetingscheduler.domain.port.out.BookingRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class BookingPersistenceAdapter implements BookingRepositoryPort {

    private static final Sort CHRONOLOGICAL = Sort.by("startTime", "id");

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

    @Override
    public PageResult<Booking> search(Long roomId, TimeSlot window, int page, int size) {
        Page<BookingJpaEntity> result = repository.findAll(
                BookingSpecifications.matching(roomId, window), PageRequest.of(page, size, CHRONOLOGICAL));
        return new PageResult<>(result.map(BookingJpaEntity::toDomain).getContent(),
                page, size, result.getTotalElements());
    }
}
