package net.space.developer.meetingscheduler.adapter.out.persistence;

import net.space.developer.meetingscheduler.domain.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

interface BookingJpaRepository extends JpaRepository<BookingJpaEntity, Long>, JpaSpecificationExecutor<BookingJpaEntity> {

    @Query("""
            select count(b) > 0 from BookingJpaEntity b
            where b.roomId = :roomId
              and b.status = :status
              and b.startTime < :end
              and b.endTime > :start
            """)
    boolean existsOverlapping(Long roomId, BookingStatus status, Instant start, Instant end);
}
