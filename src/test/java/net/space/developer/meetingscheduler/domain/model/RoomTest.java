package net.space.developer.meetingscheduler.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoomTest {

    @Test
    void newRoomHasNoId() {
        Room room = Room.create("Orion", 8, "2nd floor");

        assertThat(room.id()).isNull();
        assertThat(room.name()).isEqualTo("Orion");
        assertThat(room.capacity()).isEqualTo(8);
        assertThat(room.location()).isEqualTo("2nd floor");
    }
}
