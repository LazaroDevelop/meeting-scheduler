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
import net.space.developer.meetingscheduler.adapter.in.web.common.ApiDocs;
import net.space.developer.meetingscheduler.adapter.in.web.common.Locations;
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

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief BookingController handles HTTP requests related to bookings, including creating, searching, retrieving, and canceling bookings.
 */

@Tag(name = "Bookings")
@RestController
@RequestMapping("/api/v1/bookings")
class BookingController {

    /**
     * The maximum page size for paginated booking search results.
     */
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * The use case for creating a booking.
     */
    private final CreateBookingUseCase createBooking;

    /**
     * The use case for canceling a booking.
     */
    private final CancelBookingUseCase cancelBooking;

    /**
     * The use case for querying bookings.
     */
    private final QueryBookingsUseCase queryBookings;
    
    /**
     * Copy constructor for BookingController.
     */
    BookingController(CreateBookingUseCase createBooking, CancelBookingUseCase cancelBooking,
                      QueryBookingsUseCase queryBookings) {
        this.createBooking = createBooking;
        this.cancelBooking = cancelBooking;
        this.queryBookings = queryBookings;
    }

    /**
     * Handles the HTTP POST request to create a new booking.
     * @param request - The request body containing the booking details.
     * @return A ResponseEntity containing the created booking and the Location header pointing to the new resource.
     */
    @Operation(summary = "Book a room", description = "Fails with 409 when the room is already booked in that slot.")
    @SecurityRequirement(name = ApiDocs.BASIC_AUTH)
    @PostMapping
    ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse booking = BookingResponse.from(createBooking.create(request.toCommand()));
        return ResponseEntity.created(Locations.of(booking.id())).body(booking);
    }

    /**
     * Handles the HTTP GET request to search for bookings with optional filters for room and date, and supports pagination.
     * @param roomId - Optional filter for bookings in a specific room.
     * @param date - Optional filter for bookings overlapping a specific UTC day (yyyy-MM-dd).
     * @param page - The page number for pagination (default is 0).
     * @param size - The number of bookings per page for pagination (default is 20, maximum is 100).
     * @return A PageResponse containing the paginated list of bookings matching the search criteria.
     */
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

    /**
     * Handles the HTTP GET request to retrieve a specific booking by its unique identifier.
     * @param id - The unique identifier of the booking to retrieve.
     * @return A BookingResponse containing the details of the requested booking.
     */
    @Operation(summary = "Get a booking")
    @GetMapping("/{id}")
    BookingResponse findById(@PathVariable Long id) {
        return BookingResponse.from(queryBookings.findById(id));
    }

    /**
     * Handles the HTTP DELETE request to cancel a specific booking by its unique identifier.
     * @param id - The unique identifier of the booking to cancel.
     */
    @Operation(summary = "Cancel a booking", description = "Soft cancel; fails with 409 if already cancelled.")
    @SecurityRequirement(name = ApiDocs.BASIC_AUTH)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancel(@PathVariable Long id) {
        cancelBooking.cancel(id);
    }
}
