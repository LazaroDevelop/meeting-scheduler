package net.space.developer.meetingscheduler.application;

import net.space.developer.meetingscheduler.domain.exception.RoomAlreadyExistsException;
import net.space.developer.meetingscheduler.domain.exception.RoomNotFoundException;
import net.space.developer.meetingscheduler.domain.model.Room;
import net.space.developer.meetingscheduler.domain.port.in.CreateRoomCommand;
import net.space.developer.meetingscheduler.domain.port.out.RoomRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepositoryPort rooms;

    @InjectMocks
    private RoomService service;

    @Test
    void createsRoomWhenNameIsFree() {
        given(rooms.existsByName("Orion")).willReturn(false);
        given(rooms.save(any())).willAnswer(call -> withId(call.getArgument(0), 1L));

        Room created = service.create(new CreateRoomCommand("Orion", 8, "2nd floor"));

        assertThat(created).isEqualTo(new Room(1L, "Orion", 8, "2nd floor"));
    }

    @Test
    void rejectsDuplicateName() {
        given(rooms.existsByName("Orion")).willReturn(true);

        assertThatThrownBy(() -> service.create(new CreateRoomCommand("Orion", 8, null)))
                .isInstanceOf(RoomAlreadyExistsException.class);
        then(rooms).should(never()).save(any());
    }

    @Test
    void findsRoomById() {
        Room room = new Room(1L, "Orion", 8, null);
        given(rooms.findById(1L)).willReturn(Optional.of(room));

        assertThat(service.findById(1L)).isEqualTo(room);
    }

    @Test
    void failsWhenRoomDoesNotExist() {
        given(rooms.findById(9L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(9L)).isInstanceOf(RoomNotFoundException.class);
    }

    @Test
    void listsAllRooms() {
        List<Room> all = List.of(new Room(1L, "Orion", 8, null), new Room(2L, "Vega", 4, null));
        given(rooms.findAll()).willReturn(all);

        assertThat(service.findAll()).isEqualTo(all);
    }

    private static Room withId(Room room, Long id) {
        return new Room(id, room.name(), room.capacity(), room.location());
    }
}
