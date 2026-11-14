# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

Freshly generated Spring Boot scaffold (`net.space.developer.meetingscheduler`) — only the application class and test bootstrap exist. There is no domain code yet.

Stack: Spring Boot 4.1.x, Java 21, Maven. Starters: Web MVC, Data JPA, Liquibase, Actuator, H2 console. Runtime drivers for both H2 and PostgreSQL.

## Commands

The Maven wrapper scripts (`mvnw`, `mvnw.cmd`) are **not committed** — only `.mvn/wrapper/maven-wrapper.properties` is. Use the system `mvn` (or regenerate the wrapper with `mvn wrapper:wrapper`).

```bash
mvn clean verify                                   # build + all tests
mvn test -Dtest=MeetingSchedulerApplicationTests   # single test class
mvn test -Dtest=SomeTest#someMethod                # single test method
mvn spring-boot:run                                # run app (no DB configured — see below)
mvn spring-boot:test-run                           # run app with a Testcontainers Postgres
```

Tests and `spring-boot:test-run` require Docker running (Testcontainers).

## Architecture notes

- **Database wiring is test-driven via Testcontainers.** `TestcontainersConfiguration` provides a `postgres:latest` container with `@ServiceConnection`, so datasource properties are injected automatically. Integration tests opt in with `@Import(TestcontainersConfiguration.class)`. `TestMeetingSchedulerApplication` (in `src/test`) launches the real app against that container for local development.
- `application.properties` has no datasource configured; outside of the test launcher, Boot will fall back to embedded H2.
- **Schema is owned by Liquibase**, not Hibernate DDL. No changelog exists yet — the default location is `src/main/resources/db/changelog/db.changelog-master.yaml`; add it before adding entities, or startup will fail.
- `pom.xml` passes `-Amapstruct.defaultComponentModel=spring` to the compiler, but MapStruct is not yet a dependency — add `org.mapstruct:mapstruct` plus the `mapstruct-processor` annotation-processor path when introducing mappers. The `maven-compiler-plugin` is pinned to an old version (3.8.1).
