package net.space.developer.meetingscheduler.adapter.in.web;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * One invalid input reported in the {@code errors} array of a validation problem.
 */
record FieldViolation(String field, String message) {

    static FieldViolation from(MessageSourceResolvable error) {
        String field = switch (error) {
            case FieldError fieldError -> fieldError.getField();
            case ObjectError objectError -> objectError.getObjectName();
            default -> "request";
        };
        return new FieldViolation(field, error.getDefaultMessage());
    }
}
