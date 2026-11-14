package net.space.developer.meetingscheduler.adapter.in.web.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import net.space.developer.meetingscheduler.adapter.in.web.dto.CreateBookingRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TimeRangeValidatorTest {

    private static final Instant NINE = Instant.parse("2030-01-10T09:00:00Z");
    private static final Instant TEN = Instant.parse("2030-01-10T10:00:00Z");

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    @Test
    void acceptsEndAfterStart() {
        assertThat(validate(NINE, TEN)).isEmpty();
    }

    @Test
    void rejectsEndNotAfterStartOnEndTimeField() {
        Set<ConstraintViolation<CreateBookingRequest>> violations = validate(TEN, NINE);

        assertThat(violations).singleElement()
                .satisfies(v -> assertThat(v.getPropertyPath()).hasToString("endTime"));
    }

    @Test
    void leavesMissingBoundsToNotNull() {
        assertThat(validate(null, TEN)).allSatisfy(v -> assertThat(v.getPropertyPath()).hasToString("startTime"));
        assertThat(validate(NINE, null)).allSatisfy(v -> assertThat(v.getPropertyPath()).hasToString("endTime"));
    }

    private static Set<ConstraintViolation<CreateBookingRequest>> validate(Instant start, Instant end) {
        return validator.validate(new CreateBookingRequest(1L, "Ada", start, end));
    }
}
