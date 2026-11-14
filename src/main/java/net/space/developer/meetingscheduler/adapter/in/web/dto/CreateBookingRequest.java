package net.space.developer.meetingscheduler.adapter.in.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import net.space.developer.meetingscheduler.adapter.in.web.validation.TimeRange;
import net.space.developer.meetingscheduler.adapter.in.web.validation.ValidTimeRange;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingCommand;

import java.time.Instant;
/**
 * CreateBookingRequest is the request DTO for creating a booking.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * @param roomId - The unique identifier of the room to be booked.
 * @param personInCharge - The name of the person responsible for the booking.
 * @param startTime - The start time of the booking.
 * @param endTime - The end time of the booking.
 */

@ValidTimeRange
public record CreateBookingRequest(
        @NotNull @Positive Long roomId,
        @NotBlank @Size(max = 100) String personInCharge,
        @NotNull @Future Instant startTime,
        @NotNull Instant endTime) implements TimeRange {

    public CreateBookingCommand toCommand() {
        return new CreateBookingCommand(roomId, personInCharge.strip(), startTime, endTime);
    }
}
