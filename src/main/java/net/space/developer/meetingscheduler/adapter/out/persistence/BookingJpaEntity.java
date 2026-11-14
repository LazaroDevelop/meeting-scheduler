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

@Entity
@Table(name = "bookings")
class BookingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "person_in_charge", nullable = false, length = 100)
    private String personInCharge;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    protected BookingJpaEntity() {
    }

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

    Booking toDomain() {
        return new Booking(id, roomId, personInCharge, new TimeSlot(startTime, endTime), status);
    }
}
