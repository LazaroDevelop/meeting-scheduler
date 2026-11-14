# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

REST API for booking meeting rooms. Spring Boot 4.1, Java 21, Maven, PostgreSQL, Liquibase, Spring Security (HTTP Basic), Caffeine cache, springdoc OpenAPI, Actuator.

## Commands

The Maven wrapper scripts are not committed; use the system `mvn`. **Docker must be running**: most tests use Testcontainers (`postgres:17-alpine`).

```bash
mvn clean verify                                     # all tests + JaCoCo 85% line-coverage gate
mvn test -Dtest=BookingServiceTest                   # single class
mvn test -Dtest=BookingServiceTest#rejectsOverlappingBooking   # single method
mvn spring-boot:test-run                             # run locally against a Testcontainers Postgres
docker compose up -d --build                         # app + Postgres on :8080
./scripts/smoke-test.sh                              # 54 HTTP checks against a running instance (bash + curl)
```

Coverage report: `target/site/jacoco/index.html`. Integration tests are named `*IntegrationTest` / `*EndToEndTest` and run under Surefire (there is no Failsafe).

Default API credentials: `admin` / `admin123` (override with `APP_ADMIN_USERNAME` / `APP_ADMIN_PASSWORD`). DB via `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. Swagger UI: `/swagger-ui.html`.

## Architecture (hexagonal)

Under `net.space.developer.meetingscheduler`:

- `domain` — pure Java, no Spring/JPA. Records `Room`, `Booking`, `TimeSlot` (half-open `[start, end)`; back-to-back slots don't overlap), `PageResult`. `domain.exception` has abstract bases `ResourceNotFoundException` (→404), `ConflictException` (→409), plus `InvalidTimeRangeException` (→400). The advice maps on the **base types**, so new domain exceptions only need to extend the right base. `domain.port.in` holds one use-case interface per capability plus command/query records. `domain.port.out` holds the repository ports.
- `application` — `RoomService` and `BookingService` implement the in-ports. They own `@Transactional` and the cache annotations (cache names in `CacheNames`, which must match `spring.cache.cache-names` in `application.yaml`).
- `adapter.in.web` — controllers, request/response DTO records (mapping lives on the DTOs: `toCommand()` / `from()`), `GlobalExceptionHandler`. Shared web helpers live in `adapter.in.web.common` (`Problems`, `FieldViolation`, `Locations`, `ApiDocs`). Every error body is an RFC 9457 `ProblemDetail` built through `Problems.of(...)`, which also adds a UTC `timestamp` (callers never pass one). Validation errors add `errors: [{field, message}]`.
- `adapter.out.persistence` — package-private JPA entities and repositories behind the ports. Adapters are registered through the `@RoomAdapter` / `@BookingAdapter` stereotypes (in `annotations`, meta-annotated with `@Component`). Entities map themselves (`fromDomain` / `toDomain`). Bookings reference rooms by id, not by `@ManyToOne`.
- `config` — security, cache enabling, OpenAPI.

Key invariants:
- **Double-booking prevention**: `BookingService.create` locks the room row (`findByIdForUpdate`, `PESSIMISTIC_WRITE`) before checking `existsOverlapping` for ACTIVE bookings. `BookingFlowEndToEndTest` has a concurrent-race test for this.
- Cancel is a soft delete (`status = CANCELLED`). Cancelled bookings never block slots.
- The booking-search `date` filter means "overlaps that UTC day"; results are ordered by `startTime, id`; `size` is capped at 100.
- `@EnableCaching(order = HIGHEST_PRECEDENCE)` puts the cache advice outside the transaction, so evictions happen after commit. Keep that ordering if you touch caching.
- Security: all `GET /api/**`, `/actuator/health/**` and the OpenAPI/Swagger paths are public; everything else needs Basic auth. 401 responses come from `ProblemDetailAuthenticationEntryPoint`. Write operations are marked with `@SecurityRequirement(name = ApiDocs.BASIC_AUTH)` for the docs.

## Database

Schema is owned by Liquibase (`src/main/resources/db/changelog/`). Hibernate runs with `ddl-auto: validate`. Never edit an applied changeset; add a new numbered file under `changes/` and include it from `db.changelog-master.yaml`.

## Testing conventions

- Web slices use `@ApiSliceTest(Controller.class)` (test meta-annotation = `@WebMvcTest` + real `SecurityConfig`). Use `.with(user("admin"))` on writes.
- Persistence tests: `@DataJpaTest` + `@AutoConfigureTestDatabase(replace = NONE)` + `@Import(TestcontainersConfiguration.class, <Adapter>.class)`.
- Boot 4 test packages differ from Boot 3, e.g. `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` and `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`. The Jackson mapper is Jackson 3 (`tools.jackson.databind.json.JsonMapper`).

## Javadoc style

Main description first, then `@author` / `@since`, then `@param` / `@return`. Use only standard Javadoc tags (no Doxygen `@brief`).

## Workflow

Git flow, local only: `feature/*` branches off `develop`, merged with `--no-ff`. **Feature branches are kept, never deleted.** Releases go `release/x.y.z` → `main` (tagged `vx.y.z`) and back into `develop`. Work TDD-style with separate `test:` (red), `feat:` (green) and `refactor:` commits.
