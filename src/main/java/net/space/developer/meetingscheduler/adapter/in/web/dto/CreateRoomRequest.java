package net.space.developer.meetingscheduler.adapter.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import net.space.developer.meetingscheduler.domain.port.in.CreateRoomCommand;

public record CreateRoomRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull @Positive @Max(500) Integer capacity,
        @Size(max = 200) String location) {

    public CreateRoomCommand toCommand() {
        return new CreateRoomCommand(name.strip(), capacity, location);
    }
}
