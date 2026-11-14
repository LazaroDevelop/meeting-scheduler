package net.space.developer.meetingscheduler.adapter.out.persistence;

import net.space.developer.meetingscheduler.adapter.out.persistence.annotations.BookingAdapter;
import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.BookingStatus;
import net.space.developer.meetingscheduler.domain.model.PageResult;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;
import net.space.developer.meetingscheduler.domain.port.out.BookingRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief BookingPersistenceAdapter is an implementation of the BookingRepositoryPort that uses JPA to persist and retrieve booking data.
 */

@BookingAdapter
class BookingPersistenceAdapter implements BookingRepositoryPort {

    /**
     * A Sort object that defines the default sorting order for bookings, which is chronological based on start time and then by ID.
     */
    private static final Sort CHRONOLOGICAL = Sort.by("startTime", "id");

    /**
     * The JPA repository used for performing CRUD operations on BookingJpaEntity.
     */
    private final BookingJpaRepository repository;

    /**
     * Copy constructor for BookingPersistenceAdapter.
     */
    BookingPersistenceAdapter(BookingJpaRepository repository) {
        this.repository = repository;
    }

    /** 
     * {@inheritDoc}
    */
    @Override
    public Booking save(Booking booking) {
        return repository.save(BookingJpaEntity.fromDomain(booking)).toDomain();
    }

    /** 
     * {@inheritDoc}
    */
    @Override
    public Optional<Booking> findById(Long id) {
        return repository.findById(id).map(BookingJpaEntity::toDomain);
    }

    /** 
     * {@inheritDoc}
    */
    @Override
    public boolean existsOverlapping(Long roomId, TimeSlot slot) {
        return repository.existsOverlapping(roomId, BookingStatus.ACTIVE, slot.start(), slot.end());
    }

    /** 
     * {@inheritDoc}
    */
    @Override
    public PageResult<Booking> search(Long roomId, TimeSlot window, int page, int size) {
        Page<BookingJpaEntity> result = repository.findAll(
                BookingSpecifications.matching(roomId, window), PageRequest.of(page, size, CHRONOLOGICAL));
        return new PageResult<>(result.map(BookingJpaEntity::toDomain).getContent(),
                page, size, result.getTotalElements());
    }
}
