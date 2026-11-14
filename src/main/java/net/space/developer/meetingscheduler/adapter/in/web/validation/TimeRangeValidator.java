package net.space.developer.meetingscheduler.adapter.in.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * 
 * @brief sReports a violation on the {@code endTime} property; missing bounds are left to {@code @NotNull}.
 */
public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, TimeRange> {

    @Override
    public boolean isValid(TimeRange range, ConstraintValidatorContext context) {
        if (range == null || range.startTime() == null || range.endTime() == null
                || range.endTime().isAfter(range.startTime())) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("endTime")
                .addConstraintViolation();
        return false;
    }
}
