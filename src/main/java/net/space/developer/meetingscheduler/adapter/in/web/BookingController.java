package net.space.developer.meetingscheduler.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import net.space.developer.meetingscheduler.adapter.in.web.dto.BookingResponse;
import net.space.developer.meetingscheduler.adapter.in.web.dto.CreateBookingRequest;
import net.space.developer.meetingscheduler.adapter.in.web.dto.PageResponse;
import net.space.developer.meetingscheduler.domain.port.in.BookingSearchQuery;
import net.space.developer.meetingscheduler.domain.port.in.CancelBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryBookingsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Bookings")
@RestController
@RequestMapping("/api/v1/bookings")
class BookingController {

    private static final int MAX_PAGE_SIZE = 100;

    private final CreateBookingUseCase createBooking;
    private final CancelBookingUseCase cancelBooking;
    private final QueryBookingsUseCase queryBookings;

    BookingController(CreateBookingUseCase createBooking, CancelBookingUseCase cancelBooking,
                      QueryBookingsUseCase queryBookings) {
        this.createBooking = createBooking;
        this.cancelBooking = cancelBooking;
        this.queryBookings = queryBookings;
    }

    @Operation(summary = "Book a room", description = "Fails with 409 when the room is already booked in that slot.")
    @SecurityRequirement(name = ApiDocs.BASIC_AUTH)
    @PostMapping
    ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse booking = BookingResponse.from(createBooking.create(request.toCommand()));
        return ResponseEntity.created(Locations.of(booking.id())).body(booking);
    }

    @Operation(summary = "List bookings", description = "Ordered by start time; optionally filtered by room and UTC day.")
    @GetMapping
    PageResponse<BookingResponse> search(
            @RequestParam(required = false) @Positive Long roomId,
            @Parameter(description = "UTC day (yyyy-MM-dd); matches bookings overlapping it")
            @RequestParam(required = false) LocalDate date,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(MAX_PAGE_SIZE) int size) {
        BookingSearchQuery query = new BookingSearchQuery(roomId, date, page, size);
        return PageResponse.from(queryBookings.search(query), BookingResponse::from);
    }

    @Operation(summary = "Get a booking")
    @GetMapping("/{id}")
    BookingResponse findById(@PathVariable Long id) {
        return BookingResponse.from(queryBookings.findById(id));
    }

    @Operation(summary = "Cancel a booking", description = "Soft cancel; fails with 409 if already cancelled.")
    @SecurityRequirement(name = ApiDocs.BASIC_AUTH)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancel(@PathVariable Long id) {
        cancelBooking.cancel(id);
    }
}
