package net.space.developer.meetingscheduler.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.List;

/**
 * Single place where the API's {@link ProblemDetail} bodies are built.
 */
public final class Problems {

    static final String VALIDATION_FAILED = "Validation failed";

    private Problems() {
    }

    public static ProblemDetail of(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }

    static ProblemDetail validation(List<FieldViolation> errors) {
        ProblemDetail problem = of(HttpStatus.BAD_REQUEST, VALIDATION_FAILED, "Request contains invalid fields");
        problem.setProperty("errors", errors);
        return problem;
    }
}
