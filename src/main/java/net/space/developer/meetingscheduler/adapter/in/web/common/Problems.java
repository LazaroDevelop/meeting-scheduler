package net.space.developer.meetingscheduler.adapter.in.web.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.Instant;
import java.util.List;

/**
 * Single place where the API's {@link ProblemDetail} bodies are built.
 * Every problem carries a {@code timestamp} (UTC instant) of when it was produced.
 *
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 */
public final class Problems {

    public static final String VALIDATION_FAILED = "Validation failed";

    private Problems() {
    }

    /**
     * Builds a problem with the given status, title and detail, stamped with the current instant.
     *
     * @param status the HTTP status of the problem
     * @param title  a short, human-readable summary of the problem type
     * @param detail an explanation specific to this occurrence
     * @return the problem detail
     */
    public static ProblemDetail of(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Builds a 400 validation problem listing every invalid field.
     *
     * @param errors the invalid fields
     * @return the problem detail with an {@code errors} property
     */
    public static ProblemDetail validation(List<FieldViolation> errors) {
        ProblemDetail problem = of(HttpStatus.BAD_REQUEST, VALIDATION_FAILED, "Request contains invalid fields");
        problem.setProperty("errors", errors);
        return problem;
    }
}
