package net.space.developer.meetingscheduler.adapter.out.persistence;

import net.space.developer.meetingscheduler.TestcontainersConfiguration;
import net.space.developer.meetingscheduler.domain.model.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, RoomPersistenceAdapter.class})
class RoomPersistenceAdapterIntegrationTest {

    @Autowired
    private RoomPersistenceAdapter adapter;

    @Test
    void savesAndReadsRooms() {
        Room saved = adapter.save(Room.create("Orion", 8, "2nd floor"));

        assertThat(saved.id()).isNotNull();
        assertThat(adapter.findById(saved.id())).contains(saved);
        assertThat(adapter.findAll()).containsExactly(saved);
    }

    @Test
    void checksNameExistenceIgnoringCase() {
        adapter.save(Room.create("Orion", 8, null));

        assertThat(adapter.existsByName("orion")).isTrue();
        assertThat(adapter.existsByName("Vega")).isFalse();
    }
}
