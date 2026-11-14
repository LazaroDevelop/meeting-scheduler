package net.space.developer.meetingscheduler.adapter.in.web.common;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief One invalid input reported in the {@code errors} array of a validation problem.
 */
public record FieldViolation(String field, String message) {

    public static FieldViolation from(ObjectError error) {
        String field = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
        return new FieldViolation(field, error.getDefaultMessage());
    }
}
