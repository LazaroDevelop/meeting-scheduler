package net.space.developer.meetingscheduler;

import net.space.developer.meetingscheduler.adapter.in.web.dto.BookingResponse;
import net.space.developer.meetingscheduler.adapter.in.web.dto.RoomResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Drives the real HTTP stack (servlet container, security, validation, caching, Liquibase, PostgreSQL).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
class BookingFlowEndToEndTest {

    private static final String MORNING = """
            {"roomId": %d, "personInCharge": "%s", "startTime": "2030-03-01T09:00:00Z", "endTime": "2030-03-01T10:00:00Z"}
            """;

    @Autowired
    private RestTestClient client;

    private RestTestClient admin;

    @BeforeEach
    void authenticateAdmin() {
        admin = client.mutate().defaultHeaders(headers -> headers.setBasicAuth("admin", "admin123")).build();
    }

    @Test
    void completeBookingLifecycle() {
        RoomResponse room = createRoom();

        BookingResponse booking = book(room.id(), "Ada Lovelace");
        assertThat(booking.status().name()).isEqualTo("ACTIVE");

        admin.post().uri("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                .body(MORNING.formatted(room.id(), "Grace Hopper"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody().jsonPath("$.title").isEqualTo("Conflict");

        client.get().uri("/api/v1/bookings?roomId={id}&date=2030-03-01&size=10", room.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(1)
                .jsonPath("$.content[0].personInCharge").isEqualTo("Ada Lovelace");

        admin.delete().uri("/api/v1/bookings/{id}", booking.id())
                .exchange()
                .expectStatus().isNoContent();

        client.get().uri("/api/v1/bookings/{id}", booking.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.status").isEqualTo("CANCELLED");

        assertThat(book(room.id(), "Grace Hopper").personInCharge()).isEqualTo("Grace Hopper");
    }

    @Test
    void rejectsAnonymousWritesButServesPublicReads() {
        client.post().uri("/api/v1/rooms").contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\": \"Anonymous\", \"capacity\": 2}")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().exists("WWW-Authenticate");

        client.get().uri("/api/v1/rooms").exchange().expectStatus().isOk();
        client.get().uri("/actuator/health").exchange().expectStatus().isOk();
    }

    @Test
    void returnsClearValidationErrors() {
        admin.post().uri("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"roomId": 1, "personInCharge": "", "startTime": "2030-03-01T10:00:00Z", "endTime": "2030-03-01T09:00:00Z"}
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Validation failed")
                .jsonPath("$.errors[?(@.field == 'personInCharge')]").exists()
                .jsonPath("$.errors[?(@.field == 'endTime')]").exists();
    }

    @Test
    void returnsNotFoundForUnknownRoom() {
        admin.post().uri("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                .body(MORNING.formatted(Long.MAX_VALUE, "Ada"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void concurrentRequestsForTheSameSlotProduceExactlyOneBooking() throws Exception {
        RoomResponse room = createRoom();
        Callable<HttpStatusCode> attempt = () -> admin.post().uri("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(MORNING.formatted(room.id(), "Racer"))
                .exchange()
                .returnResult(String.class)
                .getStatus();

        List<HttpStatusCode> statuses;
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<HttpStatusCode>> futures = executor.invokeAll(IntStream.range(0, 8).mapToObj(i -> attempt).toList());
            statuses = futures.stream().map(BookingFlowEndToEndTest::join).toList();
        }

        assertThat(statuses).filteredOn(HttpStatus.CREATED::equals).hasSize(1);
        assertThat(statuses).filteredOn(HttpStatus.CONFLICT::equals).hasSize(7);
    }

    private RoomResponse createRoom() {
        return admin.post().uri("/api/v1/rooms").contentType(MediaType.APPLICATION_JSON)
                .body("{\"name\": \"Room %s\", \"capacity\": 6}".formatted(UUID.randomUUID()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RoomResponse.class)
                .returnResult().getResponseBody();
    }

    private BookingResponse book(Long roomId, String person) {
        return admin.post().uri("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                .body(MORNING.formatted(roomId, person))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookingResponse.class)
                .returnResult().getResponseBody();
    }

    private static HttpStatusCode join(Future<HttpStatusCode> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
