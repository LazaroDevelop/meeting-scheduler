package net.space.developer.meetingscheduler.adapter.out.persistence;

import net.space.developer.meetingscheduler.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class SchemaMigrationIntegrationTest {

    private static final Timestamp NINE = Timestamp.from(Instant.parse("2030-01-10T09:00:00Z"));
    private static final Timestamp TEN = Timestamp.from(Instant.parse("2030-01-10T10:00:00Z"));

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void liquibaseAppliedAllChangesets() {
        Integer applied = jdbc.queryForObject("SELECT COUNT(*) FROM databasechangelog", Integer.class);

        assertThat(applied).isGreaterThanOrEqualTo(2);
    }

    @Test
    void roomNameIsUnique() {
        insertRoom("Orion");

        assertThatThrownBy(() -> insertRoom("Orion"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void bookingEndMustBeAfterStart() {
        long roomId = insertRoom("Vega");

        assertThatThrownBy(() -> insertBooking(roomId, TEN, NINE))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void bookingRequiresExistingRoom() {
        assertThatThrownBy(() -> insertBooking(999L, NINE, TEN))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private long insertRoom(String name) {
        return jdbc.queryForObject(
                "INSERT INTO rooms (name, capacity) VALUES (?, 4) RETURNING id", Long.class, name);
    }

    private void insertBooking(long roomId, Timestamp start, Timestamp end) {
        jdbc.update("""
                INSERT INTO bookings (room_id, person_in_charge, start_time, end_time, status)
                VALUES (?, 'Ada', ?, ?, 'ACTIVE')
                """, roomId, start, end);
    }
}
