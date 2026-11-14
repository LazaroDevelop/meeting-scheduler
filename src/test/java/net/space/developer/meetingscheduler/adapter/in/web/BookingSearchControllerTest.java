package net.space.developer.meetingscheduler.adapter.in.web;

import net.space.developer.meetingscheduler.domain.model.Booking;
import net.space.developer.meetingscheduler.domain.model.BookingStatus;
import net.space.developer.meetingscheduler.domain.model.PageResult;
import net.space.developer.meetingscheduler.domain.model.TimeSlot;
import net.space.developer.meetingscheduler.domain.port.in.BookingSearchQuery;
import net.space.developer.meetingscheduler.domain.port.in.CancelBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.CreateBookingUseCase;
import net.space.developer.meetingscheduler.domain.port.in.QueryBookingsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiSliceTest(BookingController.class)
class BookingSearchControllerTest {

    private static final String BOOKINGS = "/api/v1/bookings";
    private static final Booking BOOKING = new Booking(7L, 1L, "Ada Lovelace",
            new TimeSlot(Instant.parse("2030-01-10T09:00:00Z"), Instant.parse("2030-01-10T10:00:00Z")),
            BookingStatus.ACTIVE);

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CreateBookingUseCase createBooking;

    @MockitoBean
    private CancelBookingUseCase cancelBooking;

    @MockitoBean
    private QueryBookingsUseCase queryBookings;

    @Test
    void searchesByRoomAndDateWithPagination() throws Exception {
        given(queryBookings.search(new BookingSearchQuery(1L, LocalDate.of(2030, 1, 10), 1, 5)))
                .willReturn(new PageResult<>(List.of(BOOKING), 1, 5, 6));

        mvc.perform(get(BOOKINGS).param("roomId", "1").param("date", "2030-01-10")
                        .param("page", "1").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(7))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void usesDefaultPaginationWithoutFilters() throws Exception {
        given(queryBookings.search(new BookingSearchQuery(null, null, 0, 20)))
                .willReturn(new PageResult<>(List.of(), 0, 20, 0));

        mvc.perform(get(BOOKINGS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void rejectsPageSizeAboveLimit() throws Exception {
        mvc.perform(get(BOOKINGS).param("size", "500"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors[*].field", hasItem("size")));
    }

    @Test
    void rejectsNegativePage() throws Exception {
        mvc.perform(get(BOOKINGS).param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].field", hasItem("page")));
    }
}
