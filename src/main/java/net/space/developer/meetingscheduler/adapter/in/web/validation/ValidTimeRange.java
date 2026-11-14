package net.space.developer.meetingscheduler.adapter.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint: the {@link TimeRange#endTime()} must be strictly after {@link TimeRange#startTime()}.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeRangeValidator.class)
public @interface ValidTimeRange {

    String message() default "must be after startTime";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
