package net.space.developer.meetingscheduler.adapter.in.web;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * One invalid input reported in the {@code errors} array of a validation problem.
 */
record FieldViolation(String field, String message) {

    static FieldViolation from(ObjectError error) {
        String field = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
        return new FieldViolation(field, error.getDefaultMessage());
    }
}
