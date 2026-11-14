package net.space.developer.meetingscheduler.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.BookingStatus;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;

import java.time.Instant;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief JPA entity for representing a booking in the database.
 */
@Entity
@Table(name = "bookings")
class BookingJpaEntity {

    /**
     * The unique identifier of the booking.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique identifier of the room associated with the booking.
     */
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    /**
     * The name of the person responsible for the booking.
     */
    @Column(name = "person_in_charge", nullable = false, length = 100)
    private String personInCharge;

    /**
     * The start time of the booking.
     */
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    /**
     * The end time of the booking.
     */
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    /**
     * The status of the booking.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    protected BookingJpaEntity() {
    }

    /**
     * Creates a BookingJpaEntity from a domain Booking object.
     * @param booking The domain Booking object to convert.
     * @return A BookingJpaEntity representing the same booking.
     */
    static BookingJpaEntity fromDomain(Booking booking) {
        BookingJpaEntity entity = new BookingJpaEntity();
        entity.id = booking.id();
        entity.roomId = booking.roomId();
        entity.personInCharge = booking.personInCharge();
        entity.startTime = booking.slot().start();
        entity.endTime = booking.slot().end();
        entity.status = booking.status();
        return entity;
    }

    /**
     * Converts this BookingJpaEntity to a domain Booking object.
     * @return A Booking object representing the same booking.
     */
    Booking toDomain() {
        return new Booking(id, roomId, personInCharge, new TimeSlot(startTime, endTime), status);
    }
}
